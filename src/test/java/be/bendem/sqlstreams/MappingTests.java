package be.bendem.sqlstreams;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingTests extends BaseTests {

    static private class UserNamePass {
        String name;
        String password;

        public UserNamePass(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }

    @Test
    public void testSimpleMapping() {
        try (Stream<User> userStream = sql.query("select * from users order by id", User.class)) {
            List<User> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(2, users.size());

            Assert.assertEquals(1, users.get(0).id);
            Assert.assertEquals("bob", users.get(0).name);
            Assert.assertEquals("bob_password", users.get(0).password);
            Assert.assertEquals(false, users.get(0).activated);

            Assert.assertEquals(2, users.get(1).id);
            Assert.assertEquals("georges", users.get(1).name);
            Assert.assertEquals("georges_password", users.get(1).password);
            Assert.assertEquals(false, users.get(1).activated);
        }
    }

    @Test
    public void testColumnNamesMapping() {
        try (PreparedQuery query = sql.prepareQuery("select * from users order by id")) {
            List<UserNamePass> users = query
                .mapTo(UserNamePass.class, "name", "password")
                .collect(Collectors.toList());
            Assert.assertEquals(2, users.size());

            Assert.assertEquals("bob", users.get(0).name);
            Assert.assertEquals("bob_password", users.get(0).password);

            Assert.assertEquals("georges", users.get(1).name);
            Assert.assertEquals("georges_password", users.get(1).password);
        }
    }

    @Test
    public void testColumnIndexesMapping() {
        try (PreparedQuery query = sql.prepareQuery("select * from users order by id")) {
            List<UserNamePass> users = query
                .mapTo(UserNamePass.class, 2, 3)
                .collect(Collectors.toList());
            Assert.assertEquals(2, users.size());

            Assert.assertEquals("bob", users.get(0).name);
            Assert.assertEquals("bob_password", users.get(0).password);

            Assert.assertEquals("georges", users.get(1).name);
            Assert.assertEquals("georges_password", users.get(1).password);
        }
    }

    static class DatesAndTimes {
        private final LocalTime time;
        private final LocalDate date;
        private final LocalDateTime datetime;

        public DatesAndTimes(LocalTime time, LocalDate date, LocalDateTime datetime) {
            this.time = time;
            this.date = date;
            this.datetime = datetime;
        }
    }
    @Test
    public void testJava8TimeMappingPostgres() {
        Assume.assumeTrue(database == Database.POSTGRES);

        try (Stream<DatesAndTimes> stream = sql.query("select "
                + "time '13:14:15', "
                + "date '2016-11-13', "
                + "timestamp '2016-11-13 13:14:15'", DatesAndTimes.class)) {
            DatesAndTimes output = stream.findFirst().get();

            LocalTime date = LocalTime.of(13, 14, 15);
            LocalDate time = LocalDate.of(2016, 11, 13);
            Assert.assertEquals(date, output.time);
            Assert.assertEquals(time, output.date);
            Assert.assertEquals(LocalDateTime.of(time, date), output.datetime);
        }
    }

    @Test
    @Ignore // https://github.com/xerial/sqlite-jdbc/issues/88
    public void testJava8TimeMappingSqlite() {
        Assume.assumeTrue(database == Database.SQLITE);

        try (Stream<DatesAndTimes> stream = sql.query("select "
                + "time('13:14:15'), "
                + "date('2016-11-13'), "
                + "datetime('2016-11-13 13:14:15')", DatesAndTimes.class)) {
            DatesAndTimes output = stream.findFirst().get();

            LocalTime date = LocalTime.of(13, 14, 15);
            LocalDate time = LocalDate.of(2016, 11, 13);
            Assert.assertEquals(date, output.time);
            Assert.assertEquals(time, output.date);
            Assert.assertEquals(LocalDateTime.of(time, date), output.datetime);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoConstructorMatchingColumnMapping() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(User.class, 1);
            Assert.fail();
        }
    }

    static class NoConstructor {}
    @Test(expected = IllegalArgumentException.class)
    public void testNoConstructorMapping() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(NoConstructor.class);
            Assert.fail();
        }
    }

    static class MultipleMarkedConstructor {
        @MappingConstructor
        public MultipleMarkedConstructor() {}

        @MappingConstructor
        public MultipleMarkedConstructor(Void ignore) {}
    }
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleMarkedConstructors() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(MultipleMarkedConstructor.class);
            Assert.fail();
        }
    }

    static class MultipleNonMarkedConstructor {
        public MultipleNonMarkedConstructor(boolean bool) {}

        public MultipleNonMarkedConstructor(int i) {}
    }
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleNonMarkedConstructorsWithColumnsSpecified() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(MultipleNonMarkedConstructor.class, 1);
            Assert.fail();
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void testMultipleNonMarkedConstructors() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(MultipleNonMarkedConstructor.class);
            Assert.fail();
        }
    }

    static class InvalidConstructor {
        public InvalidConstructor(User user) {}
    }
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructorMapping() {
        try (PreparedQuery query = sql.prepareQuery("select * from users")) {
            query.mapTo(InvalidConstructor.class);
            Assert.fail();
        }
    }

}
