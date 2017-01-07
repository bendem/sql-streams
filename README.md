## Sql streams

[![Codeship Status for bendem/sql-streams](https://codeship.com/projects/55cad800-f141-0133-11e7-5a649d8f4ff2/status?branch=master)](https://codeship.com/projects/149357)

Ever wondered why it was so hard to translate that SQL query into something an [ORM] can eat?
Why every method of the [JDBC API] throws a `SQLException`?
Why getting data in and out of the JDBC API was so verbose?

What if you could query and map to a class easily?

```java
try (Stream<User> users = sql.query("select * from users where added < current_date", rs -> rs.getString("username"))) {
    // Use your username stream \o/
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

What if you could insert a bunch of data at once using a fluent API?

```java
try (PreparedBatchUpdate batch = sql.prepareBatchUpdate("insert into users (name, status) values(?, ?)")) {
    int count = batch
        .with("bob", "admin").newBatch()
        .with("paul", "user").newBatch()
        .with("georges", "user").newBatch()
        .count();

    Assert.assertEquals(3, count);
}
```

## Getting started

Binaries for this library are not hosted anywhere yet. In the meantime you can compile it yourself
```sh
git clone https://github.com/bendem/sql-streams
cd sql-streams
gradle build install
```

You can then add the dependency to your project
```xml
<dependency>
    <groupId>be.bendem</groupId>
    <artifactId>sql-streams</artifactId>
    <version>[current version]</version>
</dependency>
```
```groovy
compile 'be.bendem:sql-streams:[current version]'
```

## Development

You will need [gradle] to compile and install this library
```sh
gradle build install
```

In addition to the SQLite and H2 tests, you can run the PostgreSQL tests using
```sh
PGUSER=test PGPASSWORD=test gradle test
# or
PGFORCE=true gradle test
```

Environement variables checked for postgres tests:
+ `PGFORCE`: Forces the postgres tests to be executed
+ `PGUSER`: Connection user
+ `PGPASSWORD`: Connection password
+ `PGPORT`: Port of the database (default `5436`)
+ `PGDATABASE`: Name of the database (default `test`)

[ORM]: http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html
[JDBC API]: https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/
[gradle]: https://gradle.org/gradle-download/
