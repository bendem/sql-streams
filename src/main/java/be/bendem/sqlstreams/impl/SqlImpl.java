package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.*;
import be.bendem.sqlstreams.util.SqlBiFunction;
import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.*;
import java.util.Objects;

import javax.sql.DataSource;

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
        return prepare(QueryImpl::new, preparer);
    }

    @Override
    public Update update(SqlFunction<Connection, PreparedStatement> preparer) {
        return prepare(UpdateImpl::new, preparer);
    }

    @Override
    public BatchUpdate batchUpdate(String sql) {
        return prepare(sql, BatchUpdateImpl::new, Connection::prepareStatement);
    }

    @Override
    public Execute<PreparedStatement> execute(String sql) {
        return prepare(sql, ExecuteImpl::new, Connection::prepareStatement);
    }

    @Override
    public Execute<CallableStatement> call(String sql) {
        return prepare(sql, ExecuteImpl::new, Connection::prepareCall);
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

    @FunctionalInterface
    private interface Creator<T, S extends Statement> {
        T create(SqlImpl impl, Connection connection, S statement, boolean closeConnectionAfterAction);
    }

    private <T, S extends Statement> T prepare(Creator<T, S> creator,
                                               SqlFunction<Connection, S> statementCreator) {
        Connection connection = getConnection();
        return creator.create(
            this,
            connection,
            Wrap.get(() -> statementCreator.apply(connection)),
            closeConnectionAfterAction());
    }

    private <T, S extends Statement> T prepare(String sql,
                                               Creator<T, S> creator,
                                               SqlBiFunction<Connection, String, S> statementCreator) {
        Connection connection = getConnection();
        return creator.create(
            this,
            connection,
            Wrap.get(() -> statementCreator.apply(connection, sql)),
            closeConnectionAfterAction());
    }
}
