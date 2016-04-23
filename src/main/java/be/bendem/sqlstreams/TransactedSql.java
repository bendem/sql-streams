package be.bendem.sqlstreams;

public interface TransactedSql extends Sql {

    TransactedSql commit();

    TransactedSql rollback();

    @Override
    void close();

}
