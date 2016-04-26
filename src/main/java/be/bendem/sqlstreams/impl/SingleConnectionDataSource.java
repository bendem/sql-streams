package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.UncheckedSqlException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleConnectionDataSource extends DummyDataSource implements AutoCloseable {

    private final AtomicBoolean inUse;
    private final Connection connection;
    private final Connection proxy;

    public SingleConnectionDataSource(Connection connection) {
        this.inUse = new AtomicBoolean(false);
        this.connection = connection;
        this.proxy = (Connection) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[] { Connection.class },
            (proxy, method, args) -> {
                try {
                    if (method.getName().equals("close") && method.getParameterCount() == 0) {
                        if (!connection.getAutoCommit()) {
                            connection.rollback();
                        }
                        releaseConnection();
                        return null;
                    } else if (method.getName().equals("isClosed") && method.getParameterCount() == 0) {
                        return !inUse.get();
                    }
                    return method.invoke(connection, args);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    Exception exception;
                    if (cause instanceof SQLException) {
                        exception = new UncheckedSqlException((SQLException) cause);
                    } else {
                        exception = new RuntimeException(cause);
                    }
                    exception.addSuppressed(e);
                    throw exception;
                }
            });
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (inUse.getAndSet(true)) {
            throw new IllegalStateException("Connection already in use");
        }

        return proxy;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    private void releaseConnection() {
        Wrap.execute(() -> connection.setAutoCommit(true));
        inUse.set(false);
    }

}
