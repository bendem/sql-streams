package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.PreparedBatchUpdate;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class BatchUpdateImpl extends ParameterProviderImpl<PreparedBatchUpdate, PreparedStatement> implements PreparedBatchUpdate {

    private final Connection connection;
    private final boolean closeConnection;

    BatchUpdateImpl(Connection connection, PreparedStatement statement, boolean closeConnection) {
        super(statement);
        this.connection = connection;
        this.closeConnection = closeConnection;
    }

    @Override
    public PreparedBatchUpdate newBatch() {
        Wrap.execute(statement::addBatch);
        return this;
    }

    @Override
    public int[] counts() {
        return Wrap.get(statement::executeBatch);
    }

    @Override
    public long[] largeCounts() {
        return Wrap.get(statement::executeLargeBatch);
    }

    @Override
    public int count() {
        return IntStream.of(Wrap.get(statement::executeBatch)).sum();
    }

    @Override
    public long largeCount() {
        return LongStream.of(Wrap.get(statement::executeLargeBatch)).sum();
    }

    public void close() {
        super.close();
        if (closeConnection) {
            Wrap.execute(connection::close);
        }
    }
}
