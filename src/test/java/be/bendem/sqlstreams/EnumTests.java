package be.bendem.sqlstreams;

import be.bendem.sqlstreams.impl.SqlBindings;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class EnumTests extends BaseTests {

    public enum SomeEnum {
        VALUE_1, VALUE_2
    }

    @Test
    public void insertEnum() {
        try (Update update = sql.update(INSERT_INTO_TEST, SomeEnum.VALUE_2)) {
            Assert.assertEquals(1, update.count());
        }
        Optional<SomeEnum> inserted = sql.first("select b from test", rs -> SqlBindings.map(rs, 1, SomeEnum.class));

        Assert.assertTrue(inserted.isPresent());
        Assert.assertEquals(SomeEnum.VALUE_2, inserted.get());
    }
}
