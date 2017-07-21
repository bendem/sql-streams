package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface Update extends ParameterProvider<Update, PreparedStatement>  {

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
}
