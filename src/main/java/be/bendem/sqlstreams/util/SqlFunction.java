package be.bendem.sqlstreams.util;

import java.sql.SQLException;

public interface SqlFunction<T, R> {

    R apply(T t) throws SQLException;

}
