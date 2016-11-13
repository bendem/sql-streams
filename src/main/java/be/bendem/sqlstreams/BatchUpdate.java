package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface BatchUpdate extends ParameterProvider<BatchUpdate, PreparedStatement> {

    /**
     * Ends the current batch of parameters.
     * <p>
     * Calling this method before providing data will generally cause an exception.
     * <p>
     * Not calling this method after your last batch of parameters will cause them
     * to be ignored.
     *
     * @return {code this} for chaining
     * @see PreparedStatement#addBatch()
     */
    BatchUpdate endBatch();

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
