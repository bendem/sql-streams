package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchTests {

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
    public void testBatch() {
        try (PreparedBatchUpdate batch = sql.prepareBatchUpdate("insert into test (b) values (?)")) {
            int count = batch
                .with(2).newBatch()
                .with(3).newBatch()
                .with(4).newBatch()
                .count();

            Assert.assertEquals(3, count);
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1", rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(2, 3, 4), query.collect(Collectors.toList()));
        }
    }

}
