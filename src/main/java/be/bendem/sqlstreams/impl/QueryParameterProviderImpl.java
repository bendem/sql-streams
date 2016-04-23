package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.QueryParameterProvider;
import be.bendem.sqlstreams.SqlFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

public class QueryParameterProviderImpl<Statement extends PreparedStatement>
        extends ParameterProviderImpl<QueryParameterProvider<Statement>, Statement> implements QueryParameterProvider<Statement> {

    private final Connection connection;
    private final boolean closeConnection;

    public QueryParameterProviderImpl(Connection connection, Statement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public <R> Stream<R> mapToClass(Class<R> clazz) {
        return map(ClassMapping.get(clazz));
    }

    @Override
    public <R> Stream<R> map(SqlFunction<ResultSet, R> mapping) {
        return SqlImpl.streamFromResultSet(mapping, Wrap.get(statement::executeQuery))
            .onClose(() -> Wrap.execute(() -> {
                statement.close();
                if(closeConnection) {
                    connection.close();
                }
            }));
    }
}
