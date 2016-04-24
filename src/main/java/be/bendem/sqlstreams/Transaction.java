package be.bendem.sqlstreams;

public interface Transaction extends Sql {

    Transaction commit();

    Transaction rollback();

    @Override
    void close();

}
