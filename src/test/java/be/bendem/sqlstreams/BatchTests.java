package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchTests extends BaseTests {

    @Test
    public void testCount() {
        try (PreparedBatchUpdate batch = sql.prepareBatchUpdate(INSERT_INTO_TEST)) {
            int count = batch
                .with(2).endBatch()
                .with(3).endBatch()
                .with(4).endBatch()
                .count();

            Assert.assertEquals(3, count);
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1", rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(2, 3, 4), query.collect(Collectors.toList()));
        }
    }

    @Test
    public void testCounts() {
        try (PreparedBatchUpdate batch = sql.prepareBatchUpdate(INSERT_INTO_TEST)) {
            int[] counts = batch
                .with(2).endBatch()
                .with(3).endBatch()
                .with(4).endBatch()
                .counts();

            Assert.assertArrayEquals(new int[]{1, 1, 1}, counts);
        }

        try (Stream<Integer> query = sql.query("select b from test order by 1", rs -> rs.getInt(1))) {
            Assert.assertEquals(Arrays.asList(2, 3, 4), query.collect(Collectors.toList()));
        }
    }

}
