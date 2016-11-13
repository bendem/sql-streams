package be.bendem.sqlstreams;

import be.bendem.sqlstreams.impl.SqlImpl;
import be.bendem.sqlstreams.util.SingleConnectionDataSource;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.SqlSupplier;
import be.bendem.sqlstreams.util.SuppliedConnectionsDataSource;
import be.bendem.sqlstreams.util.Tuple2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

/**
 * This class is the main entry point from this library. It provides static
 * methods to set it up from a {@link Connection} or a {@link DataSource}
 * as well as all methods to query it.
 */
public interface Sql extends AutoCloseable {

    /**
     * Constructs a {@link Sql} instance holding a single connection.
     *
     * @param connection the connection to use
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(Connection connection) {
        return connect(new SingleConnectionDataSource(connection));
    }

    /**
     * Constructs a {@link Sql} instance retrieving new {@link Connection}s
     * from the provided {@link SqlSupplier Supplier} as needed.
     *
     * @param connectionSupplier an object supplying connections
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(SqlSupplier<Connection> connectionSupplier) {
        return connect(new SuppliedConnectionsDataSource(connectionSupplier));
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

    /**
     * Opens a new transaction bound to a single connection.
     *
     * @return the new transaction
     */
    Transaction transaction();

    Query query(SqlFunction<Connection, PreparedStatement> preparer);

    default Query query(String sql) {
        return query(conn -> conn.prepareStatement(sql));
    }

    /**
     * Prepares a query to be executed and provides it the given parameters.
     * <p>
     * Note that the query is not actually executed until a mapping method
     * of {@link Query} is called.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and map the query result
     */
    default Query query(String sql, Object... parameters) {
        return query(sql).with(parameters);
    }

    Update update(SqlFunction<Connection, PreparedStatement> preparer);

    default Update update(String sql) {
        return update(conn -> conn.prepareStatement(sql));
    }

    /**
     * Prepares a DML sql statement and provides it the given parameters.
     * <p>
     * Not that the query is not actually executed until you invoke a
     * method from {@link Update}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and retrieve the number
     *         of rows affected by this query
     */
    default Update update(String sql, Object... parameters) {
        return update(sql).with(parameters);
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

    UpdateReturning updateReturning(String sql);

    /**
     * Prepares a query and provides it the given parameters.
     * <p>
     * Note that this method is not executed until you call {@link Execute#execute()}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and execute the query
     */
    Execute<PreparedStatement> execute(String sql, Object... parameters);

    /**
     * Prepares a call and provides it the given parameters.
     * <p>
     * Note that this method is not executed until you call {@link Execute#execute()}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and execute the query
     * @see Connection#prepareCall(String)
     */
    Execute<CallableStatement> call(String sql, Object... parameters);

    /**
     * Shortcut for {@link #query(String, Object...) query(sql).first(mapping)}.
     *
     * @param sql the sql query
     * @param mapping a function to map each row to an object
     * @param <R> the type of the elements of the returned stream
     * @return a stream of elements mapped from the result set
     * @see #query(String, Object...)
     * @see Query#first(SqlFunction)
     */
    default <R> Optional<R> first(String sql, SqlFunction<ResultSet, R> mapping) {
        try (Query query = query(sql)) {
            return query.first(mapping);
        }
    }

    /**
     * Shortcut for {@link #query(String, Object...) query(sql).mapJoining(mapping)}.
     *
     * @param sql the sql query
     * @param mapping a function to map each row to a tuple
     * @param <Left> the type of the objects to map to the first table
     * @param <Right> the type of the objects to map to the second table
     * @return a stream of tuples
     */
    default <Left, Right> Stream<Tuple2<Left, Right>> join(String sql, SqlFunction<ResultSet, Tuple2<Left, Right>> mapping) {
        return query(sql).mapJoining(mapping);
    }

    void close();

}
