package be.bendem.sqlstreams;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingTests {

    static private class User {
        int id;
        String email;
        String password;

        public User(int id, String email, String password) {
            this.id = id;
            this.email = email;
            this.password = password;
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
            "password char(60) not null" +
        ")").execute();

        sql.update("insert into users (email, password) values ('x@x.com', 'bcrypted password')").count();
    }

    @After
    public void teardown() throws Exception {
        sql.close();
    }

    @Test
    public void testSimpleMapping() {
        try(Stream<User> userStream = sql.query("select * from users").mapTo(User.class)) {
            List<User> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals(1, users.get(0).id);
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
        }
    }

    @Test
    public void testColumnNamesMapping() {
        try(Stream<UserEmail> userStream = sql.query("select * from users").mapTo(UserEmail.class, "email", "password")) {
            List<UserEmail> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
        }
    }

    @Test
    public void testColumnIndexesMapping() {
        try(Stream<UserEmail> userStream = sql.query("select * from users").mapTo(UserEmail.class, 2, 3)) {
            List<UserEmail> users = userStream.collect(Collectors.toList());
            Assert.assertEquals(1, users.size());
            Assert.assertEquals("x@x.com", users.get(0).email);
            Assert.assertEquals("bcrypted password", users.get(0).password);
        }
    }

}
