package row2object;

public interface RowHandler<T> {
    void handleRow(Row2ObjectListEntry<T> row);
}
