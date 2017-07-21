package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SqlFunction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.stream.Stream;

public interface Query extends ParameterProvider<Query, PreparedStatement> {

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
}
