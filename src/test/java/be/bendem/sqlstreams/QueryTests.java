package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTests extends BaseTests {

    @Test
    public void testEmptySqlQuery() {
        try (Stream<Integer> query = sql.query("select * from test").map(rs -> 0)) {
            Assert.assertEquals(0, query.count());
        }
    }

    @Test
    public void testInsertAndSqlQuery() {
        try (Update update = sql.update("insert into test (b) values (1)")) {
            Assert.assertEquals(1, update.count());
        }
        try (Update update = sql.update(INSERT_INTO_TEST).with(2)) {
            Assert.assertEquals(1, update.count());
        }
        try (Update update = sql.update(INSERT_INTO_TEST).setInt(1, 3)) {
            Assert.assertEquals(1, update.count());
        }
        try (Update update = sql.update(INSERT_INTO_TEST).with(4)) {
            Assert.assertEquals(1, update.count());
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1").map(rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testSingleConnectionDataSource() {
        Update update = sql.update(INSERT_INTO_TEST).with(1);

        try {
            sql.update("");
            Assert.fail("connection should not be available");
        } catch (IllegalStateException expected) {}

        Assert.assertEquals(1, update.count());
        update.close();
        try (Update update2 = sql.update(INSERT_INTO_TEST).with(1)) {
            Assert.assertEquals(1, update2.count());
        } catch (IllegalStateException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFirstResult() {
        Assert.assertFalse(sql.first("select * from test", rs -> 1).isPresent());
        Assert.assertTrue(sql.first("select * from users", rs -> 1).isPresent());
    }

}
