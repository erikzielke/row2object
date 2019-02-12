package row2object.errors;

public class FileStructureError {
    private String value;
    private FileStructureErrorType type;

    public FileStructureError(String value, FileStructureErrorType type) {
        this.value = value;
        this.type = type;
    }

    public enum FileStructureErrorType {
        MISSING_COLUMN
    }

    @Override
    public String toString() {
        return "FileStructureError{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
