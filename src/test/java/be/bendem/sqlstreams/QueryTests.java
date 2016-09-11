package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryTests extends BaseTests {

    @Test
    public void testEmptySqlQuery() {
        try (Stream<Integer> query = sql.query("select * from test", rs -> 0)) {
            Assert.assertEquals(0, query.count());
        }
    }

    @Test
    public void testInsertAndSqlQuery() {
        Assert.assertEquals(1, sql.update("insert into test (b) values (1)"));
        Assert.assertEquals(1, sql.update(INSERT_INTO_TEST, 2));
        try (PreparedUpdate update = sql.prepareUpdate(INSERT_INTO_TEST)) {
            Assert.assertEquals(1, update.setInt(1, 3).count());
        }
        try (PreparedUpdate update = sql.prepareUpdate(INSERT_INTO_TEST)) {
            Assert.assertEquals(1, update.with(4).count());
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1", rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(1, 2, 3, 4), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testSingleConnectionDataSource() {
        PreparedUpdate update = sql.prepareUpdate(INSERT_INTO_TEST, 1);

        try {
            sql.update("");
            Assert.fail();
        } catch (IllegalStateException expected) {}

        Assert.assertEquals(1, update.count());
        update.close();
        try {
            sql.update(INSERT_INTO_TEST, 1);
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
