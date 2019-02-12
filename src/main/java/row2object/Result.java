package row2object;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import row2object.errors.FileStructureError;
import row2object.errors.ValueError;

public class Result<T> {
    private List<T> elements = null;
    private List<FileStructureError> fileStructureErrors = new ArrayList<>();
    private TreeMap<Long, List<ValueError>> valueErrors = new TreeMap<>();

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public List<FileStructureError> getFileStructureErrors() {
        return fileStructureErrors;
    }

    public void setFileStructureErrors(List<FileStructureError> fileStructureErrors) {
        this.fileStructureErrors = fileStructureErrors;
    }

    public TreeMap<Long, List<ValueError>> getValueErrors() {
        return valueErrors;
    }

    public void setValueErrors(TreeMap<Long, List<ValueError>> valueErrors) {
        this.valueErrors = valueErrors;
    }
}
