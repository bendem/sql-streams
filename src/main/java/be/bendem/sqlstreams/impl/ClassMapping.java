package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlFunction;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

class ClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<Class<?>, ClassMapping<?>> MAPPINGS = Collections.synchronizedMap(new WeakHashMap<>());

    @SuppressWarnings("unchecked")
    public static <T> ClassMapping<T> get(Class<T> clazz) {
        return (ClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ClassMapping<>(clazz));
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T> findConstructor(Class<T> clazz, int parameters) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == parameters && checkValidParameters(constructor.getParameterTypes())) {
                return (Constructor<T>) constructor;
            }
        }
        throw new IllegalArgumentException("No constructor for " + clazz + " with " + parameters + " parameters");
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findConstructor(Class<T> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (checkValidParameters(constructor.getParameterTypes())) {
                return (Constructor<T>) constructor;
            }
        }
        throw new IllegalArgumentException("No constructor for " + clazz);
    }

    private static boolean checkValidParameters(Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes).allMatch(SqlBindings::supported);
    }

    private final WeakReference<Constructor<T>> constructor;

    private ClassMapping(Class<T> clazz) {
        this(findConstructor(clazz));
    }

    ClassMapping(Constructor<T> constructor) {
        this.constructor = new WeakReference<>(constructor);

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
        Constructor<T> constructor = this.constructor.get();
        if (constructor == null) {
            throw new IllegalStateException("constructor got gc'd, how did that happen?");
        }
        Object[] values = getValues(constructor.getParameters(), resultSet);

        try {
            return constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
