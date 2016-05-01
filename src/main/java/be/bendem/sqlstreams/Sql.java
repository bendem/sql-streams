package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.SingleConnectionDataSource;
import be.bendem.sqlstreams.impl.SqlImpl;
import be.bendem.sqlstreams.util.SuppliedConnectionsDataSource;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.SqlSupplier;
import be.bendem.sqlstreams.util.Tuple2;

import java.sql.Connection;
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
     * @param connection the connection to use
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(Connection connection) {
        return connect(new SingleConnectionDataSource(connection));
    }

    /**
     * Constructs a {@link Sql} instance retrieving new {@link Connection}s
     * from the provided {@link SqlSupplier Supplier} as needed.
     * @param connectionSupplier an object supplying connections
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(SqlSupplier<Connection> connectionSupplier) {
        return connect(new SuppliedConnectionsDataSource(connectionSupplier));
    }

    /**
     * Constructs a {@link Sql} instance retrieving new {@link Connection}s
     * from the provided {@link DataSource} as needed.
     * @param dataSource the datasource supplying connections
     * @return the newly created {@code Sql} instance
     */
    static Sql connect(DataSource dataSource) {
        return new SqlImpl(dataSource);
    }

    // TODO Add all methods overloads for prepareCall

    /**
     * Opens a new transaction bound to a single connection.
     *
     * @return the new transaction
     */
    Transaction transaction();

    PreparedQuery prepareQuery(String sql, Object... parameters);

    PreparedUpdate prepareUpdate(String sql, Object... parameters);

    //PreparedUpdateAndGet prepareUpdateAndGet(String sql, Object... parameters);

    PreparedExecute prepareExecute(String sql, Object... parameters);

    default <R> Stream<R> query(String sql, SqlFunction<ResultSet, R> mapping) {
        return prepareQuery(sql).map(mapping);
    }

    default <R> Stream<R> query(String sql, Class<R> clazz) {
        return prepareQuery(sql).mapTo(clazz);
    }

    default <Left, Right> Stream<Tuple2<Left, Right>> join(String sql, SqlFunction<ResultSet, Tuple2<Left, Right>> mapping) {
        return prepareQuery(sql).mapJoining(mapping);
    }

    default <Left, Right> Stream<Tuple2<Left, Right>> join(String sql, Class<Left> left, Class<Right> right) {
        return prepareQuery(sql).mapJoining(left, right);
    }

    default int update(String sql, Object... parameters) {
        try (PreparedUpdate update = prepareUpdate(sql, parameters)) {
            return update.count();
        }
    }

    default long largeUpdate(String sql, Object... parameters) {
        try (PreparedUpdate update = prepareUpdate(sql, parameters)) {
            return update.largeCount();
        }
    }

    //? updateAndGet(String sql); // TODO default methods based on prepareUpdateAndGet

    default void execute(String sql, Object... parameters) {
        try (PreparedExecute execute = prepareExecute(sql, parameters)) {
            execute.execute();
        }
    }

}
