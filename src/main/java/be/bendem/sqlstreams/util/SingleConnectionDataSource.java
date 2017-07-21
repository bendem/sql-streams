package be.bendem.sqlstreams.util;

import be.bendem.sqlstreams.UncheckedSqlException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wraps a single connection that is reused each time {@link #getConnection()} is called.
 *
 * Note that closing the connection you got using {@link #getConnection()} will make it available again. If it is not
 * closed, calling {@link #getConnection()} will throw an {@link IllegalStateException}.
 *
 * To actually close the connection, call {@link #close()} on this DataSource.
 *
 * Instances of this class are thread-safe, but the connections returned when calling {@link #getConnection()} aren't.
 */
public class SingleConnectionDataSource extends DummyDataSource implements Closeable {

    private final AtomicBoolean inUse;
    private final Connection connection;
    private final Connection proxy;

    /**
     * Creates a single single connection datasource.
     *
     * @param connection the underlying jdbc connection to use
     */
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
                    } else if (cause instanceof RuntimeException) {
                        cause.addSuppressed(e);
                        throw (RuntimeException) cause;
                    } else if (cause instanceof Error) {
                        cause.addSuppressed(e);
                        throw (Error) cause;
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
    public void close() {
        Wrap.execute(connection::close);
    }

    private void releaseConnection() {
        Wrap.execute(() -> connection.setAutoCommit(true));
        inUse.set(false);
    }
}
