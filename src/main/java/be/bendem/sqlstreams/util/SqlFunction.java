package be.bendem.sqlstreams.util;

import java.sql.SQLException;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface SqlFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws SQLException generally rethrown as {@link be.bendem.sqlstreams.UncheckedSqlException}
     */
    R apply(T t) throws SQLException;
}
