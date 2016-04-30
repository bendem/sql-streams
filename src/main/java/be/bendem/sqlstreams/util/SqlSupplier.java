package be.bendem.sqlstreams.util;

import java.sql.SQLException;

public interface SqlSupplier<T> {

    T get() throws SQLException;

}
