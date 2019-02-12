package row2object;

import com.univocity.parsers.common.Context;
import com.univocity.parsers.common.processor.core.Processor;
import com.univocity.parsers.common.routine.InputDimension;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvRoutines;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import row2object.columns.ColumnName;
import row2object.columns.ImportColumn;
import row2object.columns.IntegerImportColumn;
import row2object.errors.FileStructureError;
import row2object.errors.FileStructureErrorHandler;
import row2object.errors.InvalidRowHandler;
import row2object.errors.ValueError;

public class Row2Object<T> {


    private final Validator validator;
    private FileStructureErrorHandler structureErrorHandler;
    private RowHandler<T> rowHandler;
    private InvalidRowHandler invalidRowHandler;
    private ProgressHandler progressHandler;
    private Class<T> t;

    private List<ImportColumn> columns = new ArrayList<>();

    public Row2Object(Class<T> t) {
        this.t = t;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        for (Field field : t.getDeclaredFields()) {
            String columnName = field.getName();
            ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                columnName = annotation.value();
            }

            if (field.getType() == Integer.class || field.getType() == int.class) {
                columns.add(new IntegerImportColumn(columnName, field));
            } else {
                columns.add(new ImportColumn(columnName, field));
            }
        }
    }

    public static  <T> Row2Object<T> rowParser(Class<T> t) {
        return new Row2Object<T>(t);
    }

    public Result<T> parseResult(File file) {
        Result<T> result = new Result<>();
        ArrayList<T> elements = new ArrayList<>();
        result.setElements(elements);
        this.structureErrorHandler = result::setFileStructureErrors;
        this.invalidRowHandler = (index, errors) -> result.getValueErrors().put(index, errors);
        this.rowHandler = row -> elements.add(row.getData());
        parse(file);
        return result;
    }


    public Row2Object<T> structureErrorHandler(FileStructureErrorHandler fileStructureErrorHandler) {
        this.structureErrorHandler = fileStructureErrorHandler;
        return this;
    }

    public Row2Object<T> rowHandler(RowHandler<T> rowHandler) {
        this.rowHandler = rowHandler;
        return this;
    }



    public Row2Object<T> invalidRowHandler(InvalidRowHandler invalidRowHandler) {
        this.invalidRowHandler = invalidRowHandler;
        return this;
    }

    public Row2Object<T> progressHandler(ProgressHandler progressHandler) {
        this.progressHandler = progressHandler;
        return this;
    }

    public void parse(File file) {
        CsvParserSettings settings = new CsvParserSettings();
        InputDimension inputDimension = new CsvRoutines().getInputDimension(file);

        settings.setProcessor(new Processor<Context>() {
            Map<String, Integer> headerMap = new HashMap<>();

            @Override
            public void processStarted(Context context) {

            }

            @Override
            public void rowProcessed(String[] row, Context context) {
                if (context.currentRecord() == 1) {
                    boolean valid = validateStructure(row);
                    if (!valid) {
                        context.stop();
                    }
                    for (int i = 0; i < row.length; i++) {
                        String s = row[i];
                        headerMap.put(s, i);
                    }
                } else {
                    boolean valid = validateRow(context.currentRecord(), headerMap, row);
                    if (valid) {
                        T data = mapRow(headerMap, row);
                        valid = validateRowBean(context.currentRecord(), data);
                        if (valid) {
                            if (rowHandler != null) {
                                Row2ObjectListEntry<T> entry = new Row2ObjectListEntry<>();
                                entry.setIndex(context.currentRecord());
                                entry.setData(data);
                                rowHandler.handleRow(entry);
                            }
                        }

                    }
                    if (progressHandler != null) {
                        progressHandler.onProgress(ProgressHandler.ProgressState.PARSING, context.currentRecord(), inputDimension.rowCount());
                    }
                }
            }

            @Override
            public void processEnded(Context context) {

            }
        });
        CsvParser csvParser = new CsvParser(settings);

        csvParser.parse(file);
    }

    private T mapRow(Map<String, Integer> headerMap, String[] row) {
        try {
            T instance = this.t.newInstance();
            for (ImportColumn column : columns) {
                Integer integer = headerMap.get(column.getKey());
                String rawValue = row[integer];
                Field field = column.getField();
                field.setAccessible(true);
                if (field.getType() == int.class) {
                    int convertedValue = (int) column.getConvertedValue(rawValue);
                    column.getField().setInt(instance, convertedValue);
                } else {
                    Object convertedValue =  column.getConvertedValue(rawValue);
                    column.getField().set(instance, convertedValue);

                }
            }
            return instance;
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateRow(long rowIndex, Map<String, Integer> headerMap, String[] row) {
        List<ValueError> valueErrors = new ArrayList<>();

        for (ImportColumn importColumn : columns) {
            Integer columnIndex = headerMap.get(importColumn.getKey());
            String value = row[columnIndex];
            importColumn.validate(value, importColumn.getKey(), valueErrors);
        }

        if (invalidRowHandler != null) {
            if (!valueErrors.isEmpty()) {
                invalidRowHandler.handleInvalidRow(rowIndex, valueErrors);
            }
        }

        return valueErrors.isEmpty();
    }


    private boolean validateRowBean(long rowIndex, T data) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(data);
        List<ValueError> errors = new ArrayList<>();
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            errors.add(new ValueError(constraintViolation.getPropertyPath().toString(), constraintViolation.getInvalidValue(), constraintViolation.getMessage()));
        }
        if (!errors.isEmpty()) {
            if (invalidRowHandler != null) {
                invalidRowHandler.handleInvalidRow(rowIndex, errors);
            }
        }
        return constraintViolations.isEmpty();
    }



    private boolean validateStructure(String[] columns) {
        Set<String> columnNames = new HashSet<>(Arrays.asList(columns));
        List<FileStructureError> fileStructureErrors = new ArrayList<>();
        for (ImportColumn column : this.columns) {
            if (column.isRequired()) {
                if (!columnNames.contains(column.getKey())) {
                    fileStructureErrors.add(new FileStructureError(column.getKey(), FileStructureError.FileStructureErrorType.MISSING_COLUMN));
                }
            }
        }
        if (structureErrorHandler != null) {
            structureErrorHandler.onInvalidFileStructure(fileStructureErrors);
        }
        return fileStructureErrors.isEmpty();
    }


}
