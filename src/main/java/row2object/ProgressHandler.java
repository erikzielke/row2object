package row2object;

public interface ProgressHandler {
    void onProgress(ProgressState progressState, long currentIndex, long totalRows);

    enum ProgressState {
        VALIDATING_STRUCTURE,
        PARSING,
        FINISHED_INVALID_STRUCTURE,
        FINISHED_INVALID_DATA,
        FINISHED_SUCCESSFUL
    }
}
