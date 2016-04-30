package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.Wrap;

import java.sql.PreparedStatement;

/**
 * Represents an object holding an instance of {@link PreparedStatement}.
 *
 * @param <Statement> the type of the statement
 */
public interface StatementHolder<Statement extends PreparedStatement> extends AutoCloseable {

    /**
     * Returns the underlying statement.
     * @return the statement
     */
    Statement getStatement();

    default void close() {
        Wrap.execute(getStatement()::close);
    }

}
