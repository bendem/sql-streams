package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.Transaction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.Connection;

class TransactionImpl extends SqlImpl implements Transaction {

    private final Connection connection;

    TransactionImpl(Connection connection) {
        this.connection = connection;
        Wrap.execute(() -> connection.setAutoCommit(false));
    }

    TransactionImpl(Connection connection, int isolationLevel) {
        this.connection = connection;
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
