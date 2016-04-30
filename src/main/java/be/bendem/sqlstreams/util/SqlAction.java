package be.bendem.sqlstreams.util;

import java.sql.SQLException;

public interface SqlAction {

    void execute() throws SQLException;

}
