package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Execute;

import java.sql.Connection;
import java.sql.PreparedStatement;

class ExecuteImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<Execute<Statement>, Statement> implements Execute<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    ExecuteImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public boolean execute() {
        return Wrap.get(() -> {
            boolean value = statement.execute();
            statement.close();
            if (closeConnection) {
                connection.close();
            }
            return value;
        });
    }

}
