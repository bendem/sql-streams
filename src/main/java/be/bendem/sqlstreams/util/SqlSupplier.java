package be.bendem.sqlstreams.util;

import java.sql.SQLException;

/**
 * Represents a supplier of data.
 *
 * @param <T> the type of the supplied data
 */
@FunctionalInterface
public interface SqlSupplier<T> {

    /**
     * Gets a result.
     *
     * @return the result
     * @throws SQLException generally rethrown as {@link be.bendem.sqlstreams.UncheckedSqlException}
     */
    T get() throws SQLException;
}
