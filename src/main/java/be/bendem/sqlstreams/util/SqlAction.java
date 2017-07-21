package be.bendem.sqlstreams.util;

import java.sql.SQLException;

/**
 * Represents an action.
 */
@FunctionalInterface
public interface SqlAction {

    /**
     * Performs this action.
     *
     * @throws SQLException generally rethrown as {@link be.bendem.sqlstreams.UncheckedSqlException}
     */
    void execute() throws SQLException;
}
