## sql-streams

[![Codeship Status for bendem/sql-streams](https://codeship.com/projects/55cad800-f141-0133-11e7-5a649d8f4ff2/status?branch=master)](https://codeship.com/projects/149357)

`sql-streams` is a tool for the people that don't need or want to use an ORM but
don't want to deal with the [JDBC API] either. It provides a light abstraction
over JDBC without ever making it inaccessible.

## Features

+ Simple setup if you already have a `Connection` or a `DataSource` available
+ Fluent API
+ `ResultSet` is abstracted to a `Stream`
+ Classes that can be closed are `AutoCloseable`
+ `SQLException` are wrapped into `UncheckedSqlException`
+ Doesn't try to hide the JDBC primitives, they are never further than a method
call away
+ Automatic type deduction with the `with` method

## Getting started

To get started, all you need to do is to add `sql-streams` to your dependencies:
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

Once it is done, you can create an instance of `Sql` using one of the two
`connect` methods.

```java
try (Sql sql = Sql.connect(datasource)) {
    Optional<String> userEmail = sql
        .first("select email from users where user_id = ?")
        .with(userId);
}
```

## Development

You will need [gradle] to compile and install this library
```sh
gradle build install
```

In addition to the SQLite and H2 tests, you can run the tests with PostgreSQL by providing a jdbc
connection url:
```sh
PGURL=jdbc:postgresql:test gradle test
# or with user and password if not using peer authentication
PGURL=jdbc:postgresql://localhost/test PGUSER=test PGPASSWORD=test gradle test
```

[ORM]: http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html
[JDBC API]: https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/
[gradle]: https://gradle.org/install/
