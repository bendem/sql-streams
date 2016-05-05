package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.Wrap;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

@RunWith(Parameterized.class)
public abstract class BaseTests {

    protected static final String INSERT_INTO_TEST = "insert into test (b) values (?)";

    protected enum Database {
        SQLITE, POSTGRES
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        // Just cheating multi runs
        ArrayList<Object[]> data = new ArrayList<>();
        data.add(new Object[] { Database.SQLITE });

        Properties properties = System.getProperties();
        if (properties.containsKey("PG_USER") && properties.contains("PG_PASSWORD")) {
            data.add(new Object[] { Database.POSTGRES });
        }

        return data;
    }

    public Sql sql;

    @Parameterized.Parameter
    public Database database;

    @Before
    public void setup() {
        Properties properties = System.getProperties();

        switch (database) {
        case POSTGRES:
            String port = properties.getProperty("PG_PORT", "5435");
            Connection connection = Wrap.get(() -> DriverManager.getConnection(
                "jdbc:postgresql://localhost:" + port + "/test",
                properties.getProperty("PG_USER"),
                properties.getProperty("PG_PASSWORD")));
            sql = Sql.connect(connection);
            break;
        case SQLITE:
            sql = Wrap.get(() -> Sql.connect(DriverManager.getConnection("jdbc:sqlite:")));
            break;
        }

        sql.execute("create table test (" +
            "a integer primary key autoincrement not null," +
            "b integer" +
        ")");
        sql.execute("create table users (" +
            "id integer primary key autoincrement not null," +
            "name varchar(255) unique not null," +
            "password char(60) not null," +
            "activated boolean default false not null" +
        ")");
        sql.execute("create table posts (" +
            "id integer primary key autoincrement not null," +
            "user_id integer references users (id)," +
            "content text" +
        ")");

        sql.update("insert into users (name, password) values " +
            "('bob', 'bob_password')," +
            "('georges', 'georges_password')");
        sql.update("insert into posts (user_id, content) values " +
            "(1, 'whee')," +
            "(1, 'baah')," +
            "(2, 'bleh')");
    }

    @After
    public void cleanup() throws Exception {
        sql.execute("drop table test");
        sql.execute("drop table users");
        sql.execute("drop table posts");
        sql.close();
    }

    static class User {
        int id;
        String name;
        String password;
        boolean activated;

        public User(int id, String name, String password) {
            this(id, name, password, false);
        }

        @MappingConstructor
        public User(int id, String name, String password, boolean activated) {
            this.id = id;
            this.name = name;
            this.password = password;
            this.activated = activated;
        }

        public boolean isActivated() {
            return activated;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return id == user.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "User{" +
                "activated=" + activated +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
        }
    }

    static class Post {
        final int id;
        final int userId;
        final String content;

        public Post(int id, int userId, String content) {
            this.id = id;
            this.userId = userId;
            this.content = content;
        }

        public String getContent() { return content; }
        public int getId() { return id; }
        public int getUserId() { return userId; }

        @Override
        public String toString() {
            return "Post{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                '}';
        }
    }

}
