package be.bendem.sqlstreams.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Simplistic {@link javax.sql.DataSource} implementation that will return connections from the provided {@link
 * SqlSupplier}.
 */
public class SuppliedConnectionsDataSource extends DummyDataSource {

    private final SqlSupplier<Connection> connectionSupplier;

    public SuppliedConnectionsDataSource(SqlSupplier<Connection> connectionSupplier) {
        this.connectionSupplier = Objects.requireNonNull(connectionSupplier);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionSupplier.get();
    }

}
