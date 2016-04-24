package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.UncheckedSqlException;

import java.sql.SQLException;

final class Wrap {

    interface SqlAction {
        void execute() throws SQLException;
    }

    interface SqlSupplier<T> {
        T get() throws SQLException;
    }

    static void execute(SqlAction action) throws UncheckedSqlException {
        try {
            action.execute();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

    static <T> T get(SqlSupplier<T> supplier) throws UncheckedSqlException {
        try {
            return supplier.get();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }

}
