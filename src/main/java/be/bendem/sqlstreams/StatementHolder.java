package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

/**
 * Represents an object holding an instance of {@link PreparedStatement}.
 *
 * @param <Statement> the type of the statement
 */
public interface StatementHolder<Statement extends PreparedStatement> {

    /**
     * Returns the underlying statement.
     * @return the statement
     */
    Statement getStatement();

}
