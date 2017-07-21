package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.*;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

public class SqlImpl implements Sql {

    private final DataSource dataSource;
    final SqlBindings bindings;

    SqlImpl(SqlBindings bindings) {
        this.dataSource = null;
        this.bindings = bindings;
    }

    public SqlImpl(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.bindings = new SqlBindings();
    }

    protected Connection getConnection() {
        return Wrap.get(dataSource::getConnection);
    }

    protected boolean closeConnectionAfterAction() {
        return true;
    }

    @Override
    public <T> SqlImpl registerCustomBinding(Class<T> clazz,
                                             PreparedStatementBinderByIndex<T> preparedStatementBinderByIndex) {
        bindings.addMapping(clazz, null, null, preparedStatementBinderByIndex);
        return this;
    }

    @Override
    public Transaction transaction() {
        return new TransactionImpl(this);
    }

    @Override
    public Transaction transaction(Transaction.IsolationLevel isolationLevel) {
        return new TransactionImpl(this, isolationLevel.isolationLevel);
    }

    @Override
    public Query query(SqlFunction<Connection, PreparedStatement> preparer) {
        Connection connection = getConnection();
        return new QueryImpl(
            this,
            connection,
            Wrap.get(() -> preparer.apply(connection)),
            closeConnectionAfterAction());
    }

    @Override
    public Update update(SqlFunction<Connection, PreparedStatement> preparer) {
        Connection connection = getConnection();
        return  new UpdateImpl(
            this,
            connection,
            Wrap.get(() -> preparer.apply(connection)),
            closeConnectionAfterAction());
    }

    @Override
    public BatchUpdate batchUpdate(String sql) {
        Connection connection = getConnection();
        return new BatchUpdateImpl(
            this,
            connection,
            Wrap.get(() -> connection.prepareStatement(sql)),
            closeConnectionAfterAction());
    }

    @Override
    public UpdateReturning updateReturning(String sql) {
        Connection connection = getConnection();
        return new UpdateReturningImpl(
            this,
            connection,
            Wrap.get(() -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)),
            closeConnectionAfterAction());
    }

    @Override
    public Execute<PreparedStatement> execute(String sql) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            this,
            connection,
            Wrap.get(() -> connection.prepareStatement(sql)),
            closeConnectionAfterAction());
    }

    @Override
    public Execute<CallableStatement> call(String sql) {
        Connection connection = getConnection();
        return new ExecuteImpl<>(
            this,
            connection,
            Wrap.get(() -> connection.prepareCall(sql)),
            closeConnectionAfterAction());
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
