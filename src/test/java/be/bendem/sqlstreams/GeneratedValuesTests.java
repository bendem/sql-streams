package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratedValuesTests extends BaseTests {
    @Test
    public void testRetrieveGeneratedKeys() throws Exception {
        try (UpdateReturning update = sql.updateReturning("insert into test (b) values (?)").with(1)) {
            Assert.assertEquals(1, update.count());

            try (Stream<Integer> generated = update.generated(rs -> rs.getInt(1))) {
                List<Integer> collected = generated.collect(Collectors.toList());

                Assert.assertEquals(1, collected.size());
                Assert.assertEquals(1, (int) collected.get(0));
            }
        }
    }
}
