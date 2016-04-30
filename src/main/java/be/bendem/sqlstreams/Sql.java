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
 *
 */
public interface Sql extends AutoCloseable {

    static Sql connect(Connection connection) {
        return connect(new SingleConnectionDataSource(connection));
    }

    static Sql connect(SqlSupplier<Connection> connectionSupplier) {
        return connect(new SuppliedConnectionsDataSource(connectionSupplier));
    }

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
