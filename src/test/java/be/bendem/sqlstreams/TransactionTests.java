package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;

public class TransactionTests {

    private static final String INSERT = "insert into test values (?)";

    private Sql sql;

    @Before
    public void setup() throws Exception {
        sql = Sql.connect(DriverManager.getConnection("jdbc:sqlite:"));
        sql.execute("create table test (a integer)").execute();
    }

    @After
    public void teardown() throws Exception {
        sql.close();
    }

    @Test
    public void testTransaction() {
        try (Transaction transaction = sql.transaction()) {
            Assert.assertEquals(1, transaction.update(INSERT, 1).count());
            transaction.rollback();
            Assert.assertEquals(0, transaction.query("select * from test").map(rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT, 1).count());
            transaction.commit();
            Assert.assertEquals(1, transaction.query("select * from test").map(rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT, 1).count());
        } // rollback
        Assert.assertEquals(1, sql.query("select * from test").map(rs -> 0).count());
    }

    @Test
    public void emptyTransaction() {
        try (Transaction transaction = sql.transaction()) {
        }
    }

}
