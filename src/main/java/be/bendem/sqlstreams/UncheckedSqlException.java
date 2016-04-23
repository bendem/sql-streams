package be.bendem.sqlstreams;

import java.sql.SQLException;

public class UncheckedSqlException extends RuntimeException {

    public UncheckedSqlException(SQLException exception) {
        super(exception);
    }

    public UncheckedSqlException(String message, SQLException exception) {
        super(message, exception);
    }

    @Override
    public SQLException getCause() {
        return (SQLException) super.getCause();
    }
}
