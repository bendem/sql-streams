package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.SqlFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<? super Object, SqlFunction<ResultSet, ?>> MAPPINGS = new ConcurrentHashMap<>();
    public static <T> ClassMapping<T> get(Class<T> clazz) {
        return (ClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ClassMapping<>(clazz));
    }

    private final Constructor<T> constructor;

    private ClassMapping(Class<T> clazz) {
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];
    }

    @Override
    public T apply(ResultSet o) throws SQLException {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(o, i, parameters[i].getType());
        }
        try {
            return constructor.newInstance(values);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
