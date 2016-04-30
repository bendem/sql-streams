package be.bendem.sqlstreams.util;

import be.bendem.sqlstreams.UncheckedSqlException;

import java.sql.SQLException;

public final class Wrap {

    public static void execute(SqlAction action) throws UncheckedSqlException {
        try {
            action.execute();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    public static <T> T get(SqlSupplier<T> supplier) throws UncheckedSqlException {
        try {
            return supplier.get();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

}
