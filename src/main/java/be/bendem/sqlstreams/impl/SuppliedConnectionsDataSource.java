package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlSupplier;

import java.sql.Connection;
import java.sql.SQLException;

public class SuppliedConnectionsDataSource extends DummyDataSource {

    private final SqlSupplier<Connection> connectionSupplier;

    public SuppliedConnectionsDataSource(SqlSupplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionSupplier.get();
    }

}
