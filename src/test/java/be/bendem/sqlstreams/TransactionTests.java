package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Stream;

public class TransactionTests extends BaseTests {

    @Test
    public void testTransaction() {
        try (Transaction transaction = sql.transaction()) {
            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST).with(1).count());
            transaction.rollback();
            Assert.assertEquals(0, transaction.query("select * from test").map(rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST).with(1).count());
            transaction.commit();
            Assert.assertEquals(1, transaction.query("select * from test").map(rs -> 0).count());

            Assert.assertEquals(1, transaction.update(INSERT_INTO_TEST).with(1).count());
        } // rollback

        // Make sure the connection is still usable
        try (Stream<Integer> stream = sql.query("select * from test").map(rs -> 0)) {
            Assert.assertEquals(1, stream.count());
        }
    }

    @Test
    public void emptyTransaction() {
        try (Transaction transaction = sql.transaction()) {
        }
    }

    @Test
    public void emptyTransactionWithIsolationLevel() {
        try (Transaction transaction = sql.transaction(Transaction.IsolationLevel.SERIALIZABLE)) {
        }
    }

}
