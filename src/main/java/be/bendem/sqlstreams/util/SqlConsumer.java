package be.bendem.sqlstreams.util;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {

    void accept(T t) throws SQLException;

}
