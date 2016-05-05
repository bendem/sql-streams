package be.bendem.sqlstreams;

import be.bendem.sqlstreams.util.Tuple2;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JoinTests extends BaseTests {

    @Test
    public void testJoin() {
        String query = "select * from users inner join posts on users.id = posts.user_id";

        try (Stream<Tuple2<User, Post>> s = sql.join(query, User.class, Post.class)) {
            Map<User, List<Post>> postsByUsers = s.collect(Tuple2.grouping());

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
