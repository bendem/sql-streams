package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<Class<?>, ClassMapping<?>> MAPPINGS = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ClassMapping<T> get(Class<T> clazz) {
        return (ClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ClassMapping<>(clazz));
    }

    private final Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    private ClassMapping(Class<T> clazz) {
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];

        constructor.setAccessible(true);
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, i + 1, parameters[i].getType());
        }

        try {
            return constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
