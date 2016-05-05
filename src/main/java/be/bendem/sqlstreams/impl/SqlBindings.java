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

    private SqlBindings() {}

    @FunctionalInterface
    private interface ToSqlBindingWithIndex<T> {
        void bind(PreparedStatement statement, int index, T value) throws SQLException;
    }

    @FunctionalInterface
    private interface FromSqlBindingWithIndex<T> {
        T retrieve(ResultSet resultSet, int index) throws SQLException;
    }

    @FunctionalInterface
    private interface FromSqlBindingWithName<T> {
        T retrieve(ResultSet resultSet, String name) throws SQLException;
    }

    /**
     * These maps only contains classes loaded from the jre (Class#getClassLoader() == null)
     * so we don't leak classes in an environment where classloaders can be replaced.
     */
    private static final Map<Class<?>, FromSqlBindingWithIndex<?>> FROM_SQL_WITH_INDEX;
    private static final Map<Class<?>, ToSqlBindingWithIndex<?>> TO_SQL_WITH_INDEX;
    private static final Map<Class<?>, FromSqlBindingWithName<?>> FROM_SQL_WITH_NAME;

    static {
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

        FROM_SQL_WITH_INDEX = Collections.unmodifiableMap(fromWithIndex);
        TO_SQL_WITH_INDEX = Collections.unmodifiableMap(toWithIndex);
        FROM_SQL_WITH_NAME = Collections.unmodifiableMap(fromWithName);
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

    static <Statement extends PreparedStatement> Statement map(Statement stmt, Object[] params, int offset) throws SQLException {
        for (int i = 0; i < params.length; ++i) {
            map(stmt, i + offset + 1, params[i]);
        }
        return stmt;
    }

    static <T> void map(PreparedStatement stmt, int index, T value) throws SQLException {
        @SuppressWarnings("unchecked")
        ToSqlBindingWithIndex<T> toSqlBinding = (ToSqlBindingWithIndex<T>) TO_SQL_WITH_INDEX.get(value.getClass());
        if (toSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + value.getClass());
        }
        toSqlBinding.bind(stmt, index, value);
    }

    static <T> T map(ResultSet resultSet, int index, Class<T> clazz) throws SQLException {
        @SuppressWarnings("unchecked")
        FromSqlBindingWithIndex<T> fromSqlBinding = (FromSqlBindingWithIndex<T>) FROM_SQL_WITH_INDEX.get(clazz);
        if (fromSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + clazz);
        }
        T retrieved = fromSqlBinding.retrieve(resultSet, index);
        return resultSet.wasNull() ? null : retrieved;
    }

    static <T> T map(ResultSet resultSet, String name, Class<T> clazz) throws SQLException {
        @SuppressWarnings("unchecked")
        FromSqlBindingWithName<T> fromSqlBinding = (FromSqlBindingWithName<T>) FROM_SQL_WITH_NAME.get(clazz);
        if (fromSqlBinding == null) {
            throw new IllegalArgumentException("No binding for " + clazz);
        }
        T retrieved = fromSqlBinding.retrieve(resultSet, name);
        return resultSet.wasNull() ? null : retrieved;
    }

    static <T> boolean supported(Class<T> clazz) {
        return TO_SQL_WITH_INDEX.containsKey(clazz);
    }

}
