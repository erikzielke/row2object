package row2object.errors;

import java.util.List;

public interface FileStructureErrorHandler {
    void onInvalidFileStructure(List<FileStructureError> errors);
}
