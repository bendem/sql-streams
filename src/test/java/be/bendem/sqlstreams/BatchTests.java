package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchTests extends BaseTests {

    @Test
    public void testCount() {
        try (BatchUpdate batch = sql.batchUpdate(INSERT_INTO_TEST)) {
            int count = batch
                .with(2).next()
                .with(3).next()
                .with(4).next()
                .count();

            Assert.assertEquals(3, count);
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1").map(rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(2, 3, 4), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testCounts() {
        try (BatchUpdate batch = sql.batchUpdate(INSERT_INTO_TEST)) {
            int[] counts = batch
                .with(2).next()
                .with(3).next()
                .with(4).next()
                .counts();

            Assert.assertArrayEquals(new int[]{1, 1, 1}, counts);
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1").map(rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(2, 3, 4), query.collect(Collectors.toList()));
        }
    }

}
