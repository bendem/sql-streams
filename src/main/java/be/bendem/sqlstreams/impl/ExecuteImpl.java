package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Execute;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;

class ExecuteImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<Execute<Statement>, Statement> implements Execute<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    ExecuteImpl(SqlImpl sql, Connection connection, Statement statement, boolean closeConnection) {
        super(statement, sql.bindings);
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
