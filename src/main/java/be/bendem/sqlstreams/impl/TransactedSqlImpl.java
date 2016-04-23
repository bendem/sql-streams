package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.TransactedSql;

import java.sql.Connection;

class TransactedSqlImpl extends SqlImpl implements TransactedSql {

    private final Connection connection;

    TransactedSqlImpl(Connection connection) {
        super(null);
        this.connection = connection;
        Wrap.execute(() -> connection.setAutoCommit(false));
    }

    @Override
    protected Connection getConnection() {
        return connection;
    }

    @Override
    protected boolean closeConnectionAfterAction() {
        return false;
    }

    @Override
    public TransactedSql commit() {
        Wrap.execute(connection::commit);
        return this;
    }

    @Override
    public TransactedSql rollback() {
        Wrap.execute(connection::rollback);
        return this;
    }

    @Override
    public void close() {
        Wrap.execute(connection::close);
    }
}
