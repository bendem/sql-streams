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

    @SuppressWarnings("unchecked")
    protected static <T> Constructor<T> findConstructor(Class<T> clazz, int parameters) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == parameters) {
                return (Constructor<T>) constructor;
            }
        }
        throw new IllegalArgumentException("No constructor for " + clazz + " with " + parameters + " parameters");
    }

    private final Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    private ClassMapping(Class<T> clazz) {
        this((Constructor<T>) clazz.getConstructors()[0]);
    }

    protected ClassMapping(Constructor<T> constructor) {
        this.constructor = constructor;

        constructor.setAccessible(true);
    }

    protected Object[] getValues(Parameter[] parameters, ResultSet resultSet) {
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, i + 1, parameters[i].getType());
        }
        return values;
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Object[] values = getValues(constructor.getParameters(), resultSet);

        try {
            return constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
