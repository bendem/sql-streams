package be.bendem.sqlstreams;

import be.bendem.sqlstreams.impl.SingleConnectionDataSource;
import be.bendem.sqlstreams.impl.SqlImpl;
import be.bendem.sqlstreams.impl.SuppliedConnectionsDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
    Transaction transaction();

    Query<PreparedStatement> query(String sql, Object... parameters);

    Execute<PreparedStatement> execute(String sql, Object... parameters);

    Update<PreparedStatement> update(String sql, Object... parameters);

}
