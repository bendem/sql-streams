package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.*;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Spliterator;
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
    public PreparedExecute prepareExecute(String sql, Object... parameters) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            connection,
            Wrap.get(() -> SqlBindings.map(connection.prepareStatement(sql), parameters, 0)),
            closeConnectionAfterAction());
    }

    static <T> Stream<T> streamFromResultSet(SqlFunction<ResultSet, T> mapping, ResultSet resultSet) {
        return StreamSupport
            .stream(new Spliterator<T>() {
                @Override
                public boolean tryAdvance(Consumer<? super T> consumer) {
                    try {
                        if (resultSet.next()) {
                            consumer.accept(mapping.apply(resultSet));
                            return true;
                        }
                    } catch (SQLException e) {
                        throw new UncheckedSqlException(e);
                    }
                    return false;
                }

                @Override
                public Spliterator<T> trySplit() {
                    // Not supported
                    return null;
                }

                @Override
                public long estimateSize() {
                    // Not supported
                    return Long.MAX_VALUE;
                }

                @Override
                public int characteristics() {
                    return ORDERED;
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
