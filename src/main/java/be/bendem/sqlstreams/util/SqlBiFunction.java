package be.bendem.sqlstreams.util;

import java.sql.SQLException;

/**
 * Represents a function that accepts two arguments and produces a result.
 *
 * @param <T1> the type of the first argument
 * @param <T2> the type of the second argument
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface SqlBiFunction<T1, T2, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t1 the first argument
     * @param t2 the second argument
     * @return the function result
     * @throws SQLException generally rethrown as {@link be.bendem.sqlstreams.UncheckedSqlException}
     */
    R apply(T1 t1, T2 t2) throws SQLException;
}
