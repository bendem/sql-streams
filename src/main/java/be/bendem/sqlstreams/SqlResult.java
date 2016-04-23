package be.bendem.sqlstreams;

import java.sql.ResultSet;
import java.util.stream.Stream;

public interface SqlResult {

    <R> Stream<R> mapToClass(Class<R> clazz);

    <R> Stream<R> map(SqlFunction<ResultSet, R> mapping);

}
