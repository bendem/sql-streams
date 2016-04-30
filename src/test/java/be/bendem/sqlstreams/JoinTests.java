package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.Tuple2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.DriverManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class JoinTests {

    private Sql sql;

    @Before
    public void setup() throws Exception {
        sql = Sql.connect(DriverManager.getConnection("jdbc:sqlite:"));
        sql.execute("create table users (" +
            "id integer primary key autoincrement not null," +
            "name varchar(50) not null" +
        ")");
        sql.execute("create table posts (" +
            "id integer primary key autoincrement not null," +
            "user_id integer references users (id)," +
            "content text" +
        ")");

        sql.update("insert into users (name) values ('bob'), ('georges')");
        sql.update("insert into posts (user_id, content) values (1, 'whee'), (1, 'baah'), (2, 'bleh')");
    }

    @After
    public void teardown() throws Exception {
        sql.close();
    }

    private static class User {
        private final int id;
        private final String name;
        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
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
    }

    private static class Post {
        private final int id;
        private final int userId;
        private final String content;
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

    @Test
    public void testJoin() {
        try (Stream<Tuple2<User, Post>> s = sql.join("select * from users inner join posts on users.id = posts.user_id", User.class, Post.class)) {
            Map<User, List<Post>> postsByUsers = s.collect(Tuple2.grouping());
            System.out.println(postsByUsers);
            postsByUsers.forEach((user, posts) -> {
                Collections.sort(posts, Comparator.comparing(Post::getId));
                switch (user.getId()) {
                case 1:
                    Assert.assertEquals("bob", user.name);
                    Assert.assertEquals(2, posts.size());
                    Assert.assertEquals("whee", posts.get(0).content);
                    Assert.assertEquals("baah", posts.get(1).content);
                    break;
                case 2:
                    Assert.assertEquals("georges", user.name);
                    Assert.assertEquals(1, posts.size());
                    Assert.assertEquals("bleh", posts.get(0).content);
                    break;
                default:
                    Assert.fail();
                }
            });
        }
    }

}
