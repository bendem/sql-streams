package be.bendem.sqlstreams;

import java.sql.SQLException;

public interface SqlFunction<T, R> {

    R apply(T t) throws SQLException;

}
