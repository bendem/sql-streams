package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class UpdateImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<Update<Statement>, Statement> implements Update<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    UpdateImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public int count() {
        return Wrap.get(() -> {
            int count = statement.executeUpdate();
            close();
            return count;
        });
    }

    @Override
    public long largeCount() {
        return Wrap.get(() -> {
            long count = statement.executeLargeUpdate();
            close();
            return count;
        });
    }

    private void close() throws SQLException {
        statement.close();
        if(closeConnection) {
            connection.close();
        }
    }
}
