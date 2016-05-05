package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingTests {

    static private class User {
        int id;
        String email;
        String password;
        boolean activated;

        public User(int id, String email, String password) {
            this(id, email, password, false);
        }

        @MappingConstructor
        public User(int id, String email, String password, boolean activated) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.activated = activated;
        }
    }

    static private class UserEmail {
        String email;
        String password;

        public UserEmail(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    private Sql sql;

    @Before
    public void setup() throws Exception {
        sql = Sql.connect(DriverManager.getConnection("jdbc:sqlite:"));
        sql.execute("create table users (" +
            "id integer primary key autoincrement not null," +
            "email varchar(255) unique not null," +
            "password char(60) not null," +
            "activated boolean default false not null" +
        ")");

        sql.update("insert into users (email, password) values ('x@x.com', 'bcrypted password')");
    }

    @After
    public void teardown() throws Exception {
        sql.close();
    }

    @Test
    public void testSimpleMapping() {
        try (Stream<User> userStream = sql.query("select * from users", User.class)) {
            List<User> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals(1, users.get(0).id);
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
        }
    }

    @Test
    public void testColumnNamesMapping() {
        try (Stream<UserEmail> userStream = sql.prepareQuery("select * from users").mapTo(UserEmail.class, "email", "password")) {
            List<UserEmail> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
        }
    }

    @Test
    public void testColumnIndexesMapping() {
        try (Stream<UserEmail> userStream = sql.prepareQuery("select * from users").mapTo(UserEmail.class, 2, 3)) {
            List<UserEmail> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
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
    @Ignore // https://github.com/xerial/sqlite-jdbc/issues/88
    public void testJava8TimeMapping() {
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
