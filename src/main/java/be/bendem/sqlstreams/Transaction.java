package be.bendem.sqlstreams;

import java.sql.Connection;

public interface Transaction extends Sql {

    Transaction commit();

    Transaction rollback();

    Connection getConnection();

    @Override
    void close();

}
