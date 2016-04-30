package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.PreparedUpdate;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;

class UpdateImpl extends ParameterProviderImpl<PreparedUpdate, PreparedStatement> implements PreparedUpdate {

    private final Connection connection;
    private final boolean closeConnection;

    UpdateImpl(Connection connection, PreparedStatement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public int count() {
        return Wrap.get(statement::executeUpdate);
    }

    @Override
    public long largeCount() {
        return Wrap.get(statement::executeLargeUpdate);
    }

    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}
