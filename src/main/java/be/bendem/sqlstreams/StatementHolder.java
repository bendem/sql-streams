package be.bendem.sqlstreams;

import java.sql.PreparedStatement;

public interface StatementHolder<Statement extends PreparedStatement> {

    Statement getStatement();
    // TODO commit/rollback?

}
