package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.PreparedExecute;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;

class ExecuteImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<PreparedExecute<Statement>, Statement> implements PreparedExecute<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    ExecuteImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public boolean execute() {
        return super.execute();
    }

    @Override
    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }

}
