package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.PreparedBatchUpdate;
import be.bendem.sqlstreams.PreparedExecute;
import be.bendem.sqlstreams.PreparedQuery;
import be.bendem.sqlstreams.PreparedUpdate;
import be.bendem.sqlstreams.Sql;
import be.bendem.sqlstreams.Transaction;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

public class SqlImpl implements Sql {

    private final DataSource dataSource;

    SqlImpl() {
        dataSource = null;
    }

    public SqlImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    protected Connection getConnection() {
        return Wrap.get(dataSource::getConnection);
    }

    protected boolean closeConnectionAfterAction() {
        return true;
    }

    @Override
    public Transaction transaction() {
        return new TransactionImpl(getConnection());
    }

    @Override
    public <R> Stream<R> query(String sql, SqlFunction<ResultSet, R> mapping) {
        // TODO Benchmark it is actually worth it to use Statement over PreparedStatement
        Connection connection = getConnection();
        Statement statement = Wrap.get(connection::createStatement);
        return streamFromResultSet(mapping, Wrap.get(() -> statement.executeQuery(sql)))
            .onClose(() -> Wrap.execute(() -> {
                statement.close();
                if (closeConnectionAfterAction()) {
                    connection.close();
                }
            }));
    }

    @Override
    public PreparedQuery prepareQuery(String sql, Object... parameters) {
        Connection connection = getConnection();
        return new QueryImpl(
            connection,
            Wrap.get(() -> SqlBindings.map(connection.prepareStatement(sql), parameters, 0)),
            closeConnectionAfterAction());
    }

    @Override
    public PreparedUpdate prepareUpdate(String sql, Object... parameters) {
        Connection connection = getConnection();
        return  new UpdateImpl(
            connection,
            Wrap.get(() -> SqlBindings.map(connection.prepareStatement(sql), parameters, 0)),
            closeConnectionAfterAction());
    }

    @Override
    public PreparedBatchUpdate prepareBatchUpdate(String sql) {
        Connection connection = getConnection();
        return new BatchUpdateImpl(
            connection,
            Wrap.get(() -> connection.prepareStatement(sql)),
            closeConnectionAfterAction());
    }

    @Override
    public PreparedExecute<PreparedStatement> prepareExecute(String sql, Object... parameters) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            connection,
            Wrap.get(() -> SqlBindings.map(connection.prepareStatement(sql), parameters, 0)),
            closeConnectionAfterAction());
    }

    @Override
    public PreparedExecute<CallableStatement> prepareCall(String sql, Object... parameters) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            connection,
            Wrap.get(() -> SqlBindings.map(connection.prepareCall(sql), parameters, 0)),
            closeConnectionAfterAction());
    }

    static <T> Stream<T> streamFromResultSet(SqlFunction<ResultSet, T> mapping, ResultSet resultSet) {
        return StreamSupport
            .stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
                @Override
                public boolean tryAdvance(Consumer<? super T> consumer) {
                    return Wrap.get(() -> {
                        boolean hasNext;
                        if (hasNext = resultSet.next()) {
                            consumer.accept(mapping.apply(resultSet));
                        }
                        return hasNext;
                    });
                }
            }, false)
            .onClose(() -> Wrap.execute(resultSet::close));
    }

    @Override
    public void close() throws Exception {
        if (dataSource instanceof AutoCloseable) {
            ((AutoCloseable) dataSource).close();
        }
    }

}
