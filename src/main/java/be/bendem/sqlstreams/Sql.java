package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SingleConnectionDataSource;
import be.bendem.sqlstreams.impl.SqlImpl;
import be.bendem.sqlstreams.util.SuppliedConnectionsDataSource;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.SqlSupplier;
import be.bendem.sqlstreams.util.Tuple2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    /**
     * Prepares a query to be executed and provides it the given parameters.
     * <p>
     * Note that the query is not actually executed until a mapping method
     * of {@link PreparedQuery} is called.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and map the query result
     */
    PreparedQuery prepareQuery(String sql, Object... parameters);

    /**
     * Prepares a DML sql statement and provides it the given parameters.
     * <p>
     * Not that the query is not actually executed until you invoke a
     * method from {@link PreparedUpdate}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and retrieve the number
     *         of rows affected by this query
     */
    PreparedUpdate prepareUpdate(String sql, Object... parameters);

    /**
     * Prepares a DML statement to provide it multiple batches of parameters.
     * <p>
     * Not that the query is not actually executed until you invoke a
     * count method from {@link PreparedBatchUpdate}.
     *
     * @param sql the sql query
     * @return an object to parametrize the statement and retrieve counts
     *         of affected rows
     */
    PreparedBatchUpdate prepareBatchUpdate(String sql);

    //PreparedUpdateAndGet prepareUpdateAndGet(String sql, Object... parameters);

    /**
     * Prepares a query and provides it the given parameters.
     * <p>
     * Note that this method is not executed until you call {@link PreparedExecute#execute()}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and execute the query
     */
    PreparedExecute<PreparedStatement> prepareExecute(String sql, Object... parameters);

    /**
     * Prepares a call and provides it the given parameters.
     * <p>
     * Note that this method is not executed until you call {@link PreparedExecute#execute()}.
     *
     * @param sql the sql query
     * @param parameters parameters to apply in order to the provided query
     * @return an object to parametrize the statement and execute the query
     * @see Connection#prepareCall(String)
     */
    PreparedExecute<CallableStatement> prepareCall(String sql, Object... parameters);

    /**
     * Shortcut for {@link #prepareQuery(String, Object...) prepareQuery(sql).map(mapping)}.
     *
     * @param sql the sql query
     * @param mapping a function to map each row to an object
     * @param <R> the type of the elements of the returned stream
     * @return a stream of elements mapped from the result set
     * @see #prepareQuery(String, Object...)
     * @see PreparedQuery#map(SqlFunction)
     */
    default <R> Stream<R> query(String sql, SqlFunction<ResultSet, R> mapping) {
        return prepareQuery(sql).map(mapping);
    }

    /**
     * Shortcut for {@link #prepareQuery(String, Object...) prepareQuery(sql).mapTo(clazz)}.
     *
     * @param sql the sql query
     * @param clazz the class to map each row to
     * @param <R> the type of the elements of the returned stream
     * @return a stream of elements mapped from the result set
     */
    default <R> Stream<R> query(String sql, Class<R> clazz) {
        return prepareQuery(sql).mapTo(clazz);
    }

    /**
     * Shortcut for {@link #prepareQuery(String, Object...) prepareQuery(sql).mapJoining(mapping)}.
     *
     * @param sql the sql query
     * @param mapping a function to map each row to a tuple
     * @param <Left> the type of the objects to map to the first table
     * @param <Right> the type of the objects to map to the second table
     * @return a stream of tuples
     */
    default <Left, Right> Stream<Tuple2<Left, Right>> join(String sql, SqlFunction<ResultSet, Tuple2<Left, Right>> mapping) {
        return prepareQuery(sql).mapJoining(mapping);
    }

    /**
     * Shortcut for {@link #prepareQuery(String, Object...) prepareQuery(sql).mapJoining(left, right)}.
     *
     * @param sql the sql query
     * @param left the class to use to map the left of the join
     * @param right the class to use to map the right of the join
     * @param <Left> the type of the objects to map to the first table
     * @param <Right> the type of the objects to map to the second table
     * @return a stream of tuples
     */
    default <Left, Right> Stream<Tuple2<Left, Right>> join(String sql, Class<Left> left, Class<Right> right) {
        return prepareQuery(sql).mapJoining(left, right);
    }

    /**
     * Shortcut for {@link #prepareUpdate(String, Object...) prepareUpdate(sql, parameters...).count()}.
     *
     * @param sql the sql query
     * @param parameters the parameters to pass
     * @return the amount of rows updated
     * @see #prepareUpdate(String, Object...)
     * @see PreparedUpdate#count()
     */
    default int update(String sql, Object... parameters) {
        try (PreparedUpdate update = prepareUpdate(sql, parameters)) {
            return update.count();
        }
    }

    /**
     * Shortcut for {@link #prepareUpdate(String, Object...) prepareUpdate(sql, parameters...).largeCount()}.
     *
     * @param sql the sql query
     * @param parameters the parameters to pass
     * @return the amount of rows updated
     * @see #prepareUpdate(String, Object...)
     * @see PreparedUpdate#largeCount()
     */
    default long largeUpdate(String sql, Object... parameters) {
        try (PreparedUpdate update = prepareUpdate(sql, parameters)) {
            return update.largeCount();
        }
    }

    //? updateAndGet(String sql); // TODO default methods based on prepareUpdateAndGet

    /**
     * Shortcut for {@link #prepareExecute(String, Object...) prepareExecute(sql, parameters...).execute()}.
     *
     * @param sql the sql query
     * @param parameters the parameters to pass
     */
    default void execute(String sql, Object... parameters) {
        try (PreparedExecute<PreparedStatement> execute = prepareExecute(sql, parameters)) {
            execute.execute();
        }
    }

    void close();

}
