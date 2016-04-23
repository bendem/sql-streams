package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.ExecuteParameterProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;

class ExecuteParameterProviderImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<ExecuteParameterProvider<Statement>, Statement> implements ExecuteParameterProvider<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    ExecuteParameterProviderImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public boolean execute() {
        return Wrap.get(() -> {
            boolean value = statement.execute();
            statement.close();
            if(closeConnection) {
                connection.close();
            }
            return value;
        });
    }

}
