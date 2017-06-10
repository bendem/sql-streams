package be.bendem.sqlstreams;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetRetrieverByName<T> {

    T retrieve(ResultSet resultSet, String name) throws SQLException;
}
