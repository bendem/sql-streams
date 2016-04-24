package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ColumnClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<Class<?>, ColumnClassMapping<?>> MAPPINGS = new ConcurrentHashMap<>();
    @SuppressWarnings("unchecked")
    public static <T> ColumnClassMapping<T> get(Class<T> clazz, int[] columns) {
        return (ColumnClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ColumnClassMapping<>(clazz, columns));
    }

    private final Constructor<T> constructor;
    private final int[] columns;

    @SuppressWarnings("unchecked")
    private ColumnClassMapping(Class<T> clazz, int[] columns) {
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];
        this.columns = columns;

        constructor.setAccessible(true);
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        for(int i = 0; i < columns.length; i++) {
            values[i] = SqlBindings.map(resultSet, columns[i], parameters[i].getType());
        }

        try {
            return constructor.newInstance(values);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
