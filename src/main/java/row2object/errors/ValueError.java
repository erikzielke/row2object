package row2object.errors;

public class ValueError {
    private String column;
    private Object invalidValue;
    private String message;

    public ValueError(String column, Object invalidValue, String message) {
        this.column = column;
        this.invalidValue = invalidValue;
        this.message = message;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(Object invalidValue) {
        this.invalidValue = invalidValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ValueError{" +
                "column='" + column + '\'' +
                ", invalidValue=" + invalidValue +
                ", message='" + message + '\'' +
                '}';
    }
}
