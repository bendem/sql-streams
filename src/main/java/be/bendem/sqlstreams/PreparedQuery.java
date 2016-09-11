package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Tuple2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.stream.Stream;

public interface PreparedQuery extends ParameterProvider<PreparedQuery, PreparedStatement> {

    /**
     * Returns the first row of the current query using the provided mapping function.
     *
     * @param mapping the mapping function
     * @param <R> the type of the returned element
     * @return the first mapped value of this query if any
     */
    default <R> Optional<R> first(SqlFunction<ResultSet, R> mapping) {
        return map(mapping).findFirst();
    }

    /**
     * Returns the first row of the current query using the provided {@code class}.
     *
     * @param clazz the class to map to
     * @param <R> the type of the returned element
     * @return the first mapped value of this query if any
     */
    default <R> Optional<R> first(Class<R> clazz) {
        return mapTo(clazz).findFirst();
    }

    /**
     * Maps each row returned by this query using the provided mapping function.
     * <p>
     * This method should be called with a {@code try}-with-resources construct
     * to ensure that the underlying {@link java.sql.Statement} and {@link
     * ResultSet} are correctly closed.
     *
     * @param mapping the mapping function
     * @param <R> the type of the elements of the returned stream
     * @return a lazily populated stream of each element returned by the query
     */
    <R> Stream<R> map(SqlFunction<ResultSet, R> mapping);

    /**
     * Maps each row returned by this query to the provided {@code class}.
     * <p>
     * This method should be called with a {@code try}-with-resources construct
     * to ensure that the underlying {@link java.sql.Statement} and {@link
     * ResultSet} are correctly closed.
     * <p>
     * TODO Explain how mapping and constructor finding works
     *
     * @param clazz the class to map each row to
     * @param <R> the type of the class to map to
     * @return a lazily populated stream of each element returned by the query
     */
    <R> Stream<R> mapTo(Class<R> clazz);

    /**
     * Maps each row returned by this query to the provided {@code class}.
     * <p>
     * This method should be called with a {@code try}-with-resources construct
     * to ensure that the underlying {@link java.sql.Statement} and {@link
     * ResultSet} are correctly closed.
     * <p>
     * TODO Explain what names is used for
     * TODO Explain how mapping and constructor finding works
     *
     * @param clazz the class to map each row to
     * @param names todo
     * @param <R> the type of the class to map to
     * @return a lazily populated stream of each element returned by the query
     */
    <R> Stream<R> mapTo(Class<R> clazz, String... names);

    /**
     * Maps each row returned by this query to the provided {@code class}.
     * <p>
     * This method should be called with a {@code try}-with-resources construct
     * to ensure that the underlying {@link java.sql.Statement} and {@link
     * ResultSet} are correctly closed.
     * <p>
     * TODO Explain what columns is used for
     * TODO Explain how mapping and constructor finding works
     *
     * @param clazz the class to map each row to
     * @param columns todo
     * @param <R> the type of the class to map to
     * @return a lazily populated stream of each element returned by the query
     */
    <R> Stream<R> mapTo(Class<R> clazz, int... columns);

    <Left, Right> Stream<Tuple2<Left, Right>> mapJoining(Class<Left> leftClass, Class<Right> rightClass);

    <Left, Right> Stream<Tuple2<Left, Right>> mapJoining(SqlFunction<ResultSet, Tuple2<Left, Right>> mapping);

}
