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

    enum IsolationLevel {
        NONE(Connection.TRANSACTION_NONE),
        READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
        READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
        REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
        SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

        public final int isolationLevel;

        IsolationLevel(int isolationLevel) {
            this.isolationLevel = isolationLevel;
        }
    }

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
