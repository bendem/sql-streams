package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.PreparedQuery;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Tuple2;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

class QueryImpl extends ParameterProviderImpl<PreparedQuery, PreparedStatement> implements PreparedQuery {

    private final Connection connection;
    private final boolean closeConnection;

    QueryImpl(Connection connection, PreparedStatement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public <Left, Right> Stream<Tuple2<Left, Right>> mapJoining(SqlFunction<ResultSet, Tuple2<Left, Right>> mapping) {
        return map(mapping);
    }

    @Override
    public <R> Stream<R> map(SqlFunction<ResultSet, R> mapping) {
        return SqlImpl.streamFromResultSet(mapping, Wrap.get(statement::executeQuery))
            .onClose(this::close);
    }

    @Override
    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}
