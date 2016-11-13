/**
 * This package contains the public API for the {@code sql-streams} package.
 * <p>
 * {@code sql-streams} abstracts the {@link java.sql JDBC API} by providing tools to
 * easily handle set parameters of prepared statements and map database result sets to
 * classes.
 * <p>
 * The heart of this library is the {@link be.bendem.sqlstreams.Sql} class which provides
 * static factories as well as the methods used to execute DML and DDL sql queries. These
 * methods generally come in two flavors: prepared and non prepared.
 * <p>
 * The prepared methods return a {@code Prepared_____} instance implementing the {@link
 * be.bendem.sqlstreams.ParameterProvider} interface so as to allow you to set your query
 * parameters before executing your query. You'll most likely want to use the {@link
 * be.bendem.sqlstreams.ParameterProvider#with(java.lang.Object...)} method to set
 * parameters of the most common types.
 * <p>
 * The non prepared methods are generally shortcuts for the prepared methods.
 * <p>
 * <strong>You should make sure to always close instances you get from the library that
 * implement {@link java.lang.AutoCloseable} (applies to {@link java.util.stream.Stream}s
 * as well). This is generally best done using the {@code try}-with-resources construct
 * as such:</strong>
 * <pre>{@code try (PreparedUpdate update = sql.update("update salaries set amount = amount * 2")) {
 *     // use update here
 * }}</pre>
 *
 * <h2>Transactions</h2>
 * Transactions are handled using {@link be.bendem.sqlstreams.Sql#transaction()}. Unlike
 * the JDBC API, closing a transaction (and thus the underlying connection) is guaranteed
 * to rollback your current transaction. As such, transactional code is best written
 * using the {@code try}-with-resources construct as such:
 * <pre>{@code try(Transaction transaction = sql.transaction()) {
 *     transaction.update("update salaries set amount = amount * 2");
 *     transaction.commit();
 * } catch (UncheckedSqlException e) {
 *     // Handle the exception
 * } // automatic rollback of uncommitted data}</pre>
 *
 * <h2>Mapping</h2>
 * This library offers a few facilities to map each row of a result set to a class.
 *
 * <h3 id="magic.mapping">Auto-magic mapping</h3>
 * Magic mapping provides automatic mapping between sql types and java types for java
 * primitives (byte, short, int, long, boolean), their boxed equivalent, {@link
 * java.lang.String}, {@link java.sql.Date}, {@link java.sql.Time}, {@link
 * java.sql.Timestamp}, {@link java.time.LocalDate}, {@link java.time.LocalTime} and
 * {@link java.time.LocalDateTime}.
 *
 * <h3>Manual mapping</h3>
 * If you need a more complex mapping method, you can use {@link
 * be.bendem.sqlstreams.Query#map(be.bendem.sqlstreams.util.SqlFunction)} to map
 * each row of the result set using your own code.
 *
 * <h3>Materializing a join query as a Stream of tuples</h3>
 * {@link be.bendem.sqlstreams.Query#mapJoining(be.bendem.sqlstreams.util.SqlFunction)}
 * provides a way to materialize a join query.
 */
package be.bendem.sqlstreams;
