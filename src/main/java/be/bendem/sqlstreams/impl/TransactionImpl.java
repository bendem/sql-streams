package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Transaction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;

class TransactionImpl extends SqlImpl implements Transaction {

    private final Connection connection;

    TransactionImpl(SqlImpl sql) {
        super(sql.bindings);
        this.connection = sql.getConnection();
        Wrap.execute(() -> connection.setAutoCommit(false));
    }

    TransactionImpl(SqlImpl sql, int isolationLevel) {
        super(sql.bindings);
        this.connection = sql.getConnection();
        Wrap.execute(() -> {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
        });
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    protected boolean closeConnectionAfterAction() {
        return false;
    }

    @Override
    public Transaction commit() {
        Wrap.execute(connection::commit);
        return this;
    }

    @Override
    public Transaction rollback() {
        Wrap.execute(connection::rollback);
        return this;
    }

    @Override
    public void close() {
        Wrap.execute(() -> {
            connection.rollback();
            connection.close();
        });
    }
}
