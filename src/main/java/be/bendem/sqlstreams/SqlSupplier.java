package be.bendem.sqlstreams;

import java.sql.SQLException;

public interface SqlSupplier<T> {

    T get() throws SQLException;

}
