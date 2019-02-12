package row2object.errors;

import java.util.List;

public interface InvalidRowHandler {
    void handleInvalidRow(long index, List<ValueError> errors);
}
