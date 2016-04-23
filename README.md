## Sql streams

Ever wondered why it was so hard to translate that SQL query into something in ORM can eat?
Why every method of the JDBC API throws a SQLException?
Why getting data in and out of the JDBC API was so verbose?

What if you could write something like this:

```java
try (Stream<User> users = sql.query("select * from users where added < current_date").mapTo(User.class)) {
    // Use your users stream \o/
}
```
