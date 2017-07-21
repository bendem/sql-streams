package be.bendem.sqlstreams.util;

public interface Closeable extends AutoCloseable {

    @Override
    void close();
}
