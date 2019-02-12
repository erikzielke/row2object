package row2object.columns;

import java.lang.reflect.Field;
import java.util.List;
import row2object.errors.ValueError;

public class ImportColumn {
    private String key;
    private boolean required = true;
    private Field field;

    public ImportColumn(String key, Field field) {
        this.key = key;
        this.field = field;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getConvertedValue(Object rawValue) {
        return rawValue;
    }

    public void validate(Object value, String columnName, List<ValueError> valueErrors) {

    }
}
