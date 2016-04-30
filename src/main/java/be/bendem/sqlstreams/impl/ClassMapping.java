package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Tuple2;

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

    static <T1, T2> Tuple2<T1, T2> combine(ResultSet rs, ClassMapping<T1> t1, ClassMapping<T2> t2) throws SQLException {
        Object[] values1 = t1.getValues(0, t1.constructor.get().getParameters(), rs);
        Object[] values2 = t2.getValues(values1.length, t2.constructor.get().getParameters(), rs);
        return new Tuple2<>(
            t1.instanciate(values1),
            t2.instanciate(values2));
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

    protected Object[] getValues(Parameter[] parameters, ResultSet resultSet) throws SQLException {
        return getValues(0, parameters, resultSet);
    }

    private Object[] getValues(int offset, Parameter[] parameters, ResultSet resultSet) throws SQLException {
        Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, i + offset + 1, parameters[i].getType());
        }
        return values;
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Constructor<T> constructor = this.constructor.get();
        if (constructor == null) {
            throw new IllegalStateException("constructor got gc'd, how did that happen?");
        }
        return instanciate(getValues(constructor.getParameters(), resultSet));
    }

    private T instanciate(Object[] values) {
        try {
            return constructor.get().newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
