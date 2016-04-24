package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ColumnNamesClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<Class<?>, ColumnNamesClassMapping<?>> MAPPINGS = new ConcurrentHashMap<>();
    @SuppressWarnings("unchecked")
    public static <T> ColumnNamesClassMapping<T> get(Class<T> clazz, String[] columnNames) {
        return (ColumnNamesClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ColumnNamesClassMapping<>(clazz, columnNames));
    }

    private final Constructor<T> constructor;
    private final String[] columnNames;

    @SuppressWarnings("unchecked")
    private ColumnNamesClassMapping(Class<T> clazz, String[] columnNames) {
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];
        this.columnNames = columnNames;

        constructor.setAccessible(true);
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        for(int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, columnNames[i], parameters[i].getType());
        }

        try {
            return constructor.newInstance(values);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
