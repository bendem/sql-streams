package be.bendem.sqlstreams;

import java.sql.Connection;

/**
 * Represents a transaction bound to a specific {@link Connection}.
 * <p>
 * This object implements {@link AutoCloseable} so that you can write code like
 * <pre>{@code try (Transaction tr = sql.transaction()) {
 *     // Code in transaction
 * } // Automatic rollback
 * }</pre>
 */
public interface Transaction extends Sql {

    /**
     * Commits the current transaction.
     *
     * @return {@code this} for chaining
     * @see Connection#commit()
     */
    Transaction commit();

    /**
     * Rollbacks the current transaction.
     *
     * @return {@code this} for chaining
     * @see Connection#rollback()
     */
    Transaction rollback();

    /**
     * Gets the underlying {@link Connection} of this transaction.
     *
     * @return the underlying Connection object
     */
    Connection getConnection();

    /**
     * Closes the current transaction, rolling back any changes not committed.
     * <p>
     * Closing the transaction closes the underlying connection.
     */
    @Override
    void close();

}
