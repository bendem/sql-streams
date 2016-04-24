package be.bendem.sqlstreams.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SqlBindings {

    private interface ToSqlBinding<T> {
        void bind(PreparedStatement statement, int index, T value) throws SQLException;
    }

    private interface FromSqlBinding<T> {
        T retrieve(ResultSet resultSet, int index) throws SQLException;
    }

    private static final SqlBindings INSTANCE = new SqlBindings();

    static <T> void map(PreparedStatement stmt, int index, T value) {
        ToSqlBinding<T> toSqlBinding = (ToSqlBinding<T>) INSTANCE.toSql.get(value.getClass());
        if(toSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + value.getClass());
        }
        Wrap.execute(() -> toSqlBinding.bind(stmt, index, value));
    }

    static <T> T map(ResultSet resultSet, int index, Class<T> clazz) {
        FromSqlBinding<T> fromSqlBinding = (FromSqlBinding<T>) INSTANCE.fromSql.get(clazz);
        if(fromSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + clazz.getClass());
        }
        return Wrap.get(() -> fromSqlBinding.retrieve(resultSet, index));
    }

    private final Map<Class<?>, FromSqlBinding<?>> fromSql;
    private final Map<Class<?>, ToSqlBinding<?>> toSql;

    private SqlBindings() {
        Map<Class<?>, FromSqlBinding<?>> from = new HashMap<>();
        Map<Class<?>, ToSqlBinding<?>> to = new HashMap<>();

        addMapping(from, to, String.class, ResultSet::getString, PreparedStatement::setString);
        addMapping(from, to, Date.class, ResultSet::getDate, PreparedStatement::setDate);

        addMapping(from, to, Long.class, ResultSet::getLong, PreparedStatement::setLong);
        addMapping(from, to, Integer.class, ResultSet::getInt, PreparedStatement::setInt);
        addMapping(from, to, Short.class, ResultSet::getShort, PreparedStatement::setShort);
        addMapping(from, to, Byte.class, ResultSet::getByte, PreparedStatement::setByte);

        fromSql = Collections.unmodifiableMap(from);
        toSql = Collections.unmodifiableMap(to);
    }

    private static <T> void addMapping(Map<Class<?>, FromSqlBinding<?>> fromMap, Map<Class<?>, ToSqlBinding<?>> toMap,
                                       Class<T> clazz, FromSqlBinding<T> from, ToSqlBinding<T> to) {
        fromMap.put(clazz, from);
        toMap.put(clazz, to);
    }
}
