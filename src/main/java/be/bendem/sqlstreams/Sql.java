package be.bendem.sqlstreams;

import be.bendem.sqlstreams.impl.SqlImpl;
import be.bendem.sqlstreams.util.Closeable;
import be.bendem.sqlstreams.util.SingleConnectionDataSource;
import be.bendem.sqlstreams.util.SqlFunction;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * This class is the main entry point from this library. It provides static
 * methods to set it up from a {@link Connection} or a {@link DataSource}
 * as well as all methods to query it.
 */
public interface Sql extends Closeable {

    /**
     * Constructs a {@link Sql} instance holding a single connection.
     *
     * @param connection the connection to use
     * @return the newly created {@code Sql} instance
     * @see #connect(DataSource)
     */
    static Sql connect(Connection connection) {
        return connect(new SingleConnectionDataSource(connection));
    }

    /**
     * Constructs a {@link Sql} instance retrieving new {@link Connection}s
     * from the provided {@link DataSource} as needed.
     *
     * @param dataSource the datasource supplying connections
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(DataSource dataSource) {
        return new SqlImpl(dataSource);
    }

    <T> Sql registerCustomBinding(Class<T> clazz, PreparedStatementBinderByIndex<T> preparedStatementBinderByIndex);

    /**
     * Opens a new transaction bound to a single connection.
     *
     * @return the new transaction
     */
    Transaction transaction();

    /**
     * Opens a new transaction bound to a single connection.
     *
     * @param isolationLevel the isolation level of the underlying transaction
     * @return the new transaction
     * @see Connection#setTransactionIsolation(int)
     */
    Transaction transaction(Transaction.IsolationLevel isolationLevel);

    /**
     * Manually prepares a query from a {@link Connection}.
     *
     * @param preparer the code creating a {@link PreparedStatement} from a
     *                 {@link Connection}
     * @return an object to parametrize the statement and map the query result
     */
    Query query(SqlFunction<Connection, PreparedStatement> preparer);

    /**
     * Prepares a query to be executed.
     * <p>
     * Note that the query is not actually executed until a mapping method
     * of {@link Query} is called.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and map the query result
     */
    default Query query(String sql) {
        return query(conn -> conn.prepareStatement(sql));
    }

    /**
     * Manually prepares a DML query from a {@link Connection}.
     *
     * @param preparer the code creating a {@link PreparedStatement} from a
     *                 {@link Connection}
     * @return an object to parametrize and execute the DML statement
     */
    Update update(SqlFunction<Connection, PreparedStatement> preparer);

    /**
     * Prepares a DML sql statement.
     * <p>
     * Not that the query is not actually executed until you invoke a
     * method from {@link Update}.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and retrieve the number
     *         of rows affected by this query
     */
    default Update update(String sql) {
        return update(conn -> conn.prepareStatement(sql));
    }

    /**
     * Prepares a DML statement to provide it multiple batches of parameters.
     * <p>
     * Note that the query is not actually executed until you invoke a
     * count method from {@link BatchUpdate}.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and retrieve counts
     *         of affected rows
     */
    BatchUpdate batchUpdate(String sql);

    /**
     * Prepares a query.
     * <p>
     * Note that this method is not executed until you call {@link Execute#execute()}.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and execute the query
     */
    Execute<PreparedStatement> execute(String sql);

    /**
     * Prepares a call and provides it the given parameters.
     * <p>
     * Note that this method is not executed until you call {@link Execute#execute()}.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and execute the query
     * @see Connection#prepareCall(String)
     */
    Execute<CallableStatement> call(String sql);

    /**
     * Shortcut for {@link #query(String) query(sql).with(parameters).first(mapping)}.
     *
     * @param sql the sql query
     * @param mapping a function to map each row to an object
     * @param parameters parameters to apply in order to the provided query
     * @param <R> the type of the elements of the returned stream
     * @return a stream of elements mapped from the result set
     * @see #query(String)
     * @see Query#first(SqlFunction)
     */
    default <R> Optional<R> first(String sql, SqlFunction<ResultSet, R> mapping, Object... parameters) {
        try (Query query = query(sql).with(parameters)) {
            return query.first(mapping);
        }
    }

    /**
     * Shortcut for {@link #execute(String) execute(sql).with(parameters).execute()}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     */
    default void exec(String sql, Object... parameters) {
        try (Execute<PreparedStatement> execute = execute(sql).with(parameters)) {
            execute.execute();
        }
    }

    /**
     * Closes the underlying {@link DataSource}.
     */
    void close();
}
