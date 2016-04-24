package be.bendem.sqlstreams;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

/**
 * Represents an object providing methods to transform a {@link ResultSet} into a {@link Stream}
 * of objects.
 *
 * @param <Statement> the type of the statement
 */
public interface Query<Statement extends PreparedStatement> extends ParameterProvider<Query<Statement>, Statement> {

    <R> Stream<R> mapTo(Class<R> clazz);

    <R> Stream<R> mapTo(Class<R> clazz, String... names);

    <R> Stream<R> mapTo(Class<R> clazz, int... columns);

    <R> Stream<R> map(SqlFunction<ResultSet, R> mapping);

}
