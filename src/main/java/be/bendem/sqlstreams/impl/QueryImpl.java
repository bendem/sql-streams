package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Query;
import be.bendem.sqlstreams.SqlFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

class QueryImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<Query<Statement>, Statement> implements Query<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    QueryImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public <R> Stream<R> mapTo(Class<R> clazz) {
        return map(ClassMapping.get(clazz));
    }

    @Override
    public <R> Stream<R> mapTo(Class<R> clazz, String... names) {
        return map(ColumnNamesClassMapping.get(clazz, names));
    }

    @Override
    public <R> Stream<R> mapTo(Class<R> clazz, int... columns) {
        return map(ColumnClassMapping.get(clazz, columns));
    }

    @Override
    public <R> Stream<R> map(SqlFunction<ResultSet, R> mapping) {
        return SqlImpl.streamFromResultSet(mapping, Wrap.get(statement::executeQuery))
            .onClose(() -> Wrap.execute(() -> {
                statement.close();
                if (closeConnection) {
                    connection.close();
                }
            }));
    }
}
