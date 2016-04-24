package be.bendem.sqlstreams;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

public interface Query<Statement extends PreparedStatement> extends ParameterProvider<Query<Statement>, Statement> {

    <R> Stream<R> mapToClass(Class<R> clazz);

    <R> Stream<R> map(SqlFunction<ResultSet, R> mapping);

}
