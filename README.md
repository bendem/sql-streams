## Sql streams

[![Codeship Status for bendem/sql-streams](https://codeship.com/projects/55cad800-f141-0133-11e7-5a649d8f4ff2/status?branch=master)](https://codeship.com/projects/149357)

Ever wondered why it was so hard to translate that SQL query into something in ORM can eat?
Why every method of the JDBC API throws a SQLException?
Why getting data in and out of the JDBC API was so verbose?

What if you could write something like this:

```java
try (Stream<User> users = sql.query("select * from users where added < current_date").mapTo(User.class)) {
    // Use your users stream \o/
}
```

What if a transaction could be written like this:

```java
try (Transaction transaction = sql.transaction()) {
    // Use your transaction
    transaction.commit();
} catch (SomeException e) {
    // Handle exception
} // rollback
```
