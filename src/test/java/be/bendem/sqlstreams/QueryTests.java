package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTests {

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
    public void testEmptySqlQuery() {
        try(Stream<Integer> query = sql.query("select * from test").map(rs -> 0)) {
            Assert.assertEquals(0, query.count());
        }
    }

    @Test
    public void testInsertAndSqlQuery() {
        Assert.assertEquals(1, sql.update("insert into test values (1)").count());
        Assert.assertEquals(1, sql.update(INSERT, 2).count());
        Assert.assertEquals(1, sql.update(INSERT).setInt(1, 3).count());

        try(Stream<Integer> query = sql.query("select * from test order by 1").map(rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(1, 2, 3), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testSingleConnectionDataSource() {
        Update<PreparedStatement> update = sql.update(INSERT, 1);

        try {
            sql.update("");
            Assert.fail();
        } catch(IllegalStateException e) {}

        Assert.assertEquals(1, update.count());
        try {
            sql.update(INSERT, 1);
        } catch(IllegalStateException e) {
            Assert.fail(e.getMessage());
        }
    }

}
