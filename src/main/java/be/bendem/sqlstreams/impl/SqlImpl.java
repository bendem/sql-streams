package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.*;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public Transaction transaction(Transaction.IsolationLevel isolationLevel) {
        return new TransactionImpl(getConnection(), isolationLevel.isolationLevel);
    }

    @Override
    public Query query(SqlFunction<Connection, PreparedStatement> preparer) {
        Connection connection = getConnection();
        return new QueryImpl(
            connection,
            Wrap.get(() -> preparer.apply(connection)),
            closeConnectionAfterAction());
    }

    @Override
    public Update update(SqlFunction<Connection, PreparedStatement> preparer) {
        Connection connection = getConnection();
        return  new UpdateImpl(
            connection,
            Wrap.get(() -> preparer.apply(connection)),
            closeConnectionAfterAction());
    }

    @Override
    public BatchUpdate batchUpdate(String sql) {
        Connection connection = getConnection();
        return new BatchUpdateImpl(
            connection,
            Wrap.get(() -> connection.prepareStatement(sql)),
            closeConnectionAfterAction());
    }

    @Override
    public UpdateReturning updateReturning(String sql) {
        Connection connection = getConnection();
        return new UpdateReturningImpl(
            connection,
            Wrap.get(() -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)),
            closeConnectionAfterAction());
    }

    @Override
    public Execute<PreparedStatement> execute(String sql) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            connection,
            Wrap.get(() -> connection.prepareStatement(sql)),
            closeConnectionAfterAction());
    }

    @Override
    public Execute<CallableStatement> call(String sql) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            connection,
            Wrap.get(() -> connection.prepareCall(sql)),
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
    public void close() {
        if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
            } catch (SQLException e) {
                throw new UncheckedSqlException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
