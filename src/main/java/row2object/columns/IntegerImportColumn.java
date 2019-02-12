package row2object.columns;

import java.lang.reflect.Field;
import java.util.List;
import row2object.errors.ValueError;

public class IntegerImportColumn extends ImportColumn {
    public IntegerImportColumn(String key, Field field) {
        super(key, field);
    }

    @Override
    public Object getConvertedValue(Object rawValue) {
        if (rawValue instanceof Integer) {
            return rawValue;
        } else if (rawValue instanceof String) {
            return Integer.parseInt((String) rawValue);
        } else {
            return null;
        }
    }

    @Override
    public void validate(Object value, String columnName, List<ValueError> valueErrors) {
        if (value instanceof Integer) {

        }
        else if (value instanceof String) {
            if (!((String) value).matches("\\d+")) {
                valueErrors.add(new ValueError(columnName, value, "Not a valid integer"));
            }
        }
        else {
            valueErrors.add(new ValueError(columnName, value, "Not a valid integer"));
        }

    }
}
