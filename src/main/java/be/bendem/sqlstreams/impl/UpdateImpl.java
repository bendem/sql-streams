package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Update;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;

class UpdateImpl extends ParameterProviderImpl<Update, PreparedStatement> implements Update {

    private final Connection connection;
    private final boolean closeConnection;

    UpdateImpl(SqlImpl sql, Connection connection, PreparedStatement statement, boolean closeConnection) {
        super(statement, sql.bindings);
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
