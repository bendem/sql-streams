package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assume.assumeTrue;

public class GeneratedValuesTests extends BaseTests {
    static class Pair {
        public final int a;
        public final int b;

        Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test
    public void testInsertReturning() throws Exception {
        assumeTrue(database == Database.POSTGRES);

        try (Query query = sql.query("insert into test (b) values (?) returning a, b").with(2)) {
            List<Pair> update = query
                .map(rs -> new Pair(rs.getInt(1), rs.getInt(2)))
                .collect(Collectors.toList());
            Assert.assertEquals(1, update.size());
            Assert.assertEquals(1, update.get(0).a);
            Assert.assertEquals(2, update.get(0).b);
        }
    }
}
