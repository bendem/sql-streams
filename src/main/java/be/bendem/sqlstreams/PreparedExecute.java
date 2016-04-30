package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface PreparedExecute<Statement extends PreparedStatement>
        extends ParameterProvider<PreparedExecute<Statement>, Statement> {

    /**
     * Executes this statement.
     *
     * @return {@code true} if the first result is a {@link java.sql.ResultSet}
     *         object; {@code false} if the first result is an update count or
     *         there is no result
     * @see PreparedStatement#execute()
     * @see StatementHolder#getStatement()
     */
    boolean execute();

}
