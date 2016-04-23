package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

public class SqlImpl implements Sql {

    private final DataSource dataSource;

    public SqlImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() {
        return Wrap.get(dataSource::getConnection);
    }

    protected boolean closeConnectionAfterAction() {
        return true;
    }

    private <Provider extends ParameterProvider<Provider, Statement>, Statement extends PreparedStatement> Provider bind(
            Provider parameterProvider, Object[] parameters) {
        for(int i = 0; i < parameters.length; i++) {
            parameterProvider.setMagic(i + 1, parameters[i]);
        }
        return parameterProvider;
    }

    @Override
    public TransactedSql transaction() {
        return new TransactedSqlImpl(getConnection());
    }

    @Override
    public QueryParameterProvider<PreparedStatement> query(String sql, Object... parameters) {
        Connection connection = getConnection();

        return bind(
            new QueryParameterProviderImpl<>(
                connection, Wrap.get(() -> connection.prepareStatement(sql)), closeConnectionAfterAction()),
            parameters);
    }

    @Override
    public ExecuteParameterProvider<PreparedStatement> execute(String sql, Object... parameters) {
        Connection connection = getConnection();

        return bind(
            new ExecuteParameterProviderImpl<>(
                connection, Wrap.get(() -> connection.prepareStatement(sql)), closeConnectionAfterAction()),
            parameters);
    }

    @Override
    public UpdateParameterProvider<PreparedStatement> update(String sql, Object... parameters) {
        Connection connection = getConnection();

        return bind(
            new UpdateParameterProviderImpl<>(
                connection, Wrap.get(() -> connection.prepareStatement(sql)), closeConnectionAfterAction()),
            parameters);
    }

    static <T> Stream<T> streamFromResultSet(SqlFunction<ResultSet, T> mapping, ResultSet resultSet) {
        return StreamSupport
            .stream(new Spliterator<T>() {
                @Override
                public boolean tryAdvance(Consumer<? super T> consumer) {
                    try {
                        if(resultSet.next()) {
                            consumer.accept(mapping.apply(resultSet));
                            return true;
                        }
                        resultSet.getStatement().close();
                        resultSet.close();
                    } catch(SQLException e) {
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
        if(dataSource instanceof AutoCloseable) {
            ((AutoCloseable) dataSource).close();
        }
    }

}
