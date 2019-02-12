package row2object.sources;

public interface RowSource<T> extends AutoCloseable {
    void initialize();

    void close();
}
