package be.bendem.sqlstreams.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SqlBindings {

    private interface ToSqlBindingWithIndex<T> {
        void bind(PreparedStatement statement, int index, T value) throws SQLException;
    }

    private interface FromSqlBindingWithIndex<T> {
        T retrieve(ResultSet resultSet, int index) throws SQLException;
    }

    private interface FromSqlBindingWithName<T> {
        T retrieve(ResultSet resultSet, String name) throws SQLException;
    }

    private static final SqlBindings INSTANCE = new SqlBindings();

    static <T> void map(PreparedStatement stmt, int index, T value) {
        @SuppressWarnings("unchecked")
        ToSqlBindingWithIndex<T> toSqlBinding = (ToSqlBindingWithIndex<T>) INSTANCE.toSqlWithIndex.get(value.getClass());
        if (toSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + value.getClass());
        }
        Wrap.execute(() -> toSqlBinding.bind(stmt, index, value));
    }

    static <T> T map(ResultSet resultSet, int index, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        FromSqlBindingWithIndex<T> fromSqlBinding = (FromSqlBindingWithIndex<T>) INSTANCE.fromSqlWithIndex.get(clazz);
        if (fromSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + clazz);
        }
        return Wrap.get(() -> {
            T retrieved = fromSqlBinding.retrieve(resultSet, index);
            return resultSet.wasNull() ? null : retrieved;
        });
    }

    static <T> T map(ResultSet resultSet, String name, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        FromSqlBindingWithName<T> fromSqlBinding = (FromSqlBindingWithName<T>) INSTANCE.fromSqlWithName.get(clazz);
        if (fromSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + clazz);
        }
        return Wrap.get(() -> {
            T retrieved = fromSqlBinding.retrieve(resultSet, name);
            return resultSet.wasNull() ? null : retrieved;
        });
    }

    private final Map<Class<?>, FromSqlBindingWithIndex<?>> fromSqlWithIndex;
    private final Map<Class<?>, ToSqlBindingWithIndex<?>> toSqlWithIndex;
    private final Map<Class<?>, FromSqlBindingWithName<?>> fromSqlWithName;

    private SqlBindings() {
        Map<Class<?>, FromSqlBindingWithIndex<?>> fromWithIndex = new HashMap<>();
        Map<Class<?>, ToSqlBindingWithIndex<?>> toWithIndex = new HashMap<>();
        Map<Class<?>, FromSqlBindingWithName<?>> fromWithName = new HashMap<>();

        addMapping(fromWithIndex, fromWithName, toWithIndex, Date.class, ResultSet::getDate, ResultSet::getDate, PreparedStatement::setDate);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Time.class, ResultSet::getTime, ResultSet::getTime, PreparedStatement::setTime);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Timestamp.class, ResultSet::getTimestamp, ResultSet::getTimestamp, PreparedStatement::setTimestamp);

        addMapping(fromWithIndex, fromWithName, toWithIndex, String.class, ResultSet::getString, ResultSet::getString, PreparedStatement::setString);

        addMapping(fromWithIndex, fromWithName, toWithIndex, Long.class, ResultSet::getLong, ResultSet::getLong, PreparedStatement::setLong);
        addMapping(fromWithIndex, fromWithName, toWithIndex, long.class, ResultSet::getLong, ResultSet::getLong, PreparedStatement::setLong);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Integer.class, ResultSet::getInt, ResultSet::getInt, PreparedStatement::setInt);
        addMapping(fromWithIndex, fromWithName, toWithIndex, int.class, ResultSet::getInt, ResultSet::getInt, PreparedStatement::setInt);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Short.class, ResultSet::getShort, ResultSet::getShort, PreparedStatement::setShort);
        addMapping(fromWithIndex, fromWithName, toWithIndex, short.class, ResultSet::getShort, ResultSet::getShort, PreparedStatement::setShort);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Byte.class, ResultSet::getByte, ResultSet::getByte, PreparedStatement::setByte);
        addMapping(fromWithIndex, fromWithName, toWithIndex, byte.class, ResultSet::getByte, ResultSet::getByte, PreparedStatement::setByte);
        addMapping(fromWithIndex, fromWithName, toWithIndex, Boolean.class, ResultSet::getBoolean, ResultSet::getBoolean, PreparedStatement::setBoolean);
        addMapping(fromWithIndex, fromWithName, toWithIndex, boolean.class, ResultSet::getBoolean, ResultSet::getBoolean, PreparedStatement::setBoolean);

        fromSqlWithIndex = Collections.unmodifiableMap(fromWithIndex);
        toSqlWithIndex = Collections.unmodifiableMap(toWithIndex);
        fromSqlWithName = Collections.unmodifiableMap(fromWithName);
    }

    private static <T> void addMapping(Map<Class<?>, FromSqlBindingWithIndex<?>> fromWithIndex,
                                       Map<Class<?>, FromSqlBindingWithName<?>> fromWithName,
                                       Map<Class<?>, ToSqlBindingWithIndex<?>> toWithIndex,
                                       Class<T> clazz,
                                       FromSqlBindingWithIndex<T> fromBindingWithIndex,
                                       FromSqlBindingWithName<T> fromBindingWithName,
                                       ToSqlBindingWithIndex<T> toBindingWithIndex) {
        fromWithIndex.put(clazz, fromBindingWithIndex);
        fromWithName.put(clazz, fromBindingWithName);
        toWithIndex.put(clazz, toBindingWithIndex);
    }
}
