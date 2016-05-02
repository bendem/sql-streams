package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTests {

    private static final String INSERT = "insert into test (b) values (?)";

    private Sql sql;

    @Before
    public void setup() throws Exception {
        sql = Sql.connect(DriverManager.getConnection("jdbc:sqlite:"));
        sql.execute("create table test (a integer primary key autoincrement not null, b integer)");
    }

    @After
    public void teardown() throws Exception {
        sql.close();
    }

    @Test
    public void testEmptySqlQuery() {
        try (Stream<Integer> query = sql.query("select * from test", rs -> 0)) {
            Assert.assertEquals(0, query.count());
        }
    }

    @Test
    public void testInsertAndSqlQuery() {
        Assert.assertEquals(1, sql.update("insert into test (b) values (1)"));
        Assert.assertEquals(1, sql.update(INSERT, 2));
        try (PreparedUpdate update = sql.prepareUpdate(INSERT)) {
            Assert.assertEquals(1, update.setInt(1, 3).count());
        }
        try (PreparedUpdate update = sql.prepareUpdate(INSERT)) {
            Assert.assertEquals(1, update.with(4).count());
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1", rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testSingleConnectionDataSource() {
        PreparedUpdate update = sql.prepareUpdate(INSERT, 1);

        try {
            sql.update("");
            Assert.fail();
        } catch(IllegalStateException e) {}

        Assert.assertEquals(1, update.count());
        update.close();
        try {
            sql.update(INSERT, 1);
        } catch (IllegalStateException e) {
            Assert.fail(e.getMessage());
        }
    }

}
