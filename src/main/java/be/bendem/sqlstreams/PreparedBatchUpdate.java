package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface PreparedBatchUpdate extends ParameterProvider<PreparedBatchUpdate, PreparedStatement> {

    PreparedBatchUpdate newBatch();

    /**
     * Executes the query and return the amount of rows modified by this query.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeBatch()
     */
    int[] counts();

    /**
     * Executes the query and return the amount of rows modified by this query as a long.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeLargeBatch()
     */
    long[] largeCounts();

    /**
     * Executes the query and return the sum of the amount of rows modified by each batch.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeBatch()
     */
    int count();

    /**
     * Executes the query and return the sum of the amount of rows modified by each batch.
     *
     * @return the amount of rows modified by this query
     * @see PreparedStatement#executeLargeBatch()
     */
    long largeCount();

}
