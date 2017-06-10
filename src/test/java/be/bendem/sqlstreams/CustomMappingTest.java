package be.bendem.sqlstreams;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomMappingTest extends BaseTests {

    public static class Stuff {
        private final int test;

        public Stuff(int test) {
            this.test = test;
        }
    }

    @Test
    public void testCustomMapping() {
        sql.registerCustomBinding(Stuff.class, (statement, index, value) -> statement.setInt(index, value.test));

        try (Update update = sql.update(INSERT_INTO_TEST).with(new Stuff(4)).with()) {
            assertEquals(1, update.count());
        }

        Optional<Integer> stuff = sql.first("select b from test", rs -> rs.getInt(1));

        assertTrue(stuff.isPresent());
        assertEquals(4, (int) stuff.get());
    }
}
