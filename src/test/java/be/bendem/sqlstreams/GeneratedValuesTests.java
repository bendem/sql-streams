package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assume.assumeTrue;

public class GeneratedValuesTests extends BaseTests {
    @Test
    public void testInsertReturning() throws Exception {
        assumeTrue(database == Database.POSTGRES);

        try (Query query = sql.query("insert into test (b) values (?) returning b").with(1)) {
            List<Integer> update = query.map(rs -> rs.getInt(1)).collect(Collectors.toList());
            Assert.assertEquals(1, update.size());
            Assert.assertEquals(1, (int) update.get(0));
        }
    }
}
