package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

public class TransactionTests extends BaseTests {

    @Test
    public void testTransaction() {
        try (Transaction transaction = sql.transaction()) {
            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST, 1));
            transaction.rollback();
            Assert.assertEquals(0, transaction.query("select * from test", rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST, 1));
            transaction.commit();
            Assert.assertEquals(1, transaction.query("select * from test", rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST, 1));
        } // rollback

        // Make sure the connection is still usable
        try (Stream<Integer> stream = sql.query("select * from test", rs -> 0)) {
            Assert.assertEquals(1, stream.count());
        }
    }

    @Test
    public void emptyTransaction() {
        try (Transaction transaction = sql.transaction()) {
        }
    }

}
