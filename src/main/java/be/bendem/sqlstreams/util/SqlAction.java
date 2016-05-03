package be.bendem.sqlstreams.util;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlAction {

    void execute() throws SQLException;

}
