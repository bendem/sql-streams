package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.UpdateReturning;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

public class UpdateReturningImpl extends ParameterProviderImpl<UpdateReturning, PreparedStatement>
        implements UpdateReturning {

    private final Connection connection;
    private final boolean closeConnection;

    UpdateReturningImpl(SqlImpl sql, Connection connection, PreparedStatement statement, boolean closeConnection) {
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

    @Override
    public <T> Stream<T> generated(SqlFunction<ResultSet, T> mapping) {
        return ResultSetSpliterator.stream(mapping, Wrap.get(statement::getGeneratedKeys))
            .onClose(this::close);
    }

    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}
