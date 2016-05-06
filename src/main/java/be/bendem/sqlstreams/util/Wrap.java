package be.bendem.sqlstreams.util;

import be.bendem.sqlstreams.UncheckedSqlException;

import java.sql.SQLException;

/**
 * Utility to execute code that might throw a checked {@link SQLException} so that
 * it throws {@link UncheckedSqlException} instead.
 */
public final class Wrap {

    private Wrap() {}

    /**
     * Executes a possibly throwing code, wrapping any {@code SQLException} into
     * a {@code UncheckedSqlException}.
     *
     * @param action the action to execute
     * @throws UncheckedSqlException if an {@code SQLException} was thrown
     */
    public static void execute(SqlAction action) throws UncheckedSqlException {
        try {
            action.execute();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    /**
     * Executes a possibly throwing code and returns the result, wrapping any {@code
     * SQLException} into a {@code UncheckedSqlException}.
     *
     * @param supplier the action to execute
     * @param <T> the type of the object returned
     * @return the object returned by the supplier
     * @throws UncheckedSqlException if an {@code SQLException} was thrown
     */
    public static <T> T get(SqlSupplier<T> supplier) throws UncheckedSqlException {
        try {
            return supplier.get();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

}
