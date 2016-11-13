package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SqlFunction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

public interface UpdateReturning extends ParameterProvider<UpdateReturning, PreparedStatement> {

    /**
     * Executes the query and return the amount of rows modified by this query.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeUpdate()
     */
    int count();

    /**
     * Executes the query and return the amount of rows modified by this query as a long.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeLargeUpdate()
     */
    long largeCount();

    <T> Stream<T> generated(SqlFunction<ResultSet, T> mapping);

}
