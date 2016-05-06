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

    /**
     * Executes the statement held by this object.
     *
     * @return {@code true} if the first result is a {@link java.sql.ResultSet}
     *         object; {@code false} if the first result is an update count or
     *         there is no result
     * @see PreparedStatement#execute()
     * @see StatementHolder#getStatement()
     */
    default boolean execute() {
        return Wrap.get(getStatement()::execute);
    }

    /**
     * Closes the statement held by this object.
     */
    default void close() {
        Wrap.execute(getStatement()::close);
    }

}
