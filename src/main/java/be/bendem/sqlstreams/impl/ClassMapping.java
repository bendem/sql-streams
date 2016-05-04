package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Tuple2;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
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

    private static <T> T instanciate(Constructor<T> constructor, Object[] values) {
        try {
            return constructor.newInstance(values);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static <T1, T2> Tuple2<T1, T2> combine(ResultSet rs, ClassMapping<T1> t1, ClassMapping<T2> t2) throws SQLException {
        Constructor<T1> t1Constructor = t1.getConstructor();
        Constructor<T2> t2Constructor = t2.getConstructor();
        Object[] values1 = t1.getValues(0, t1Constructor.getParameters(), rs);
        Object[] values2 = t2.getValues(values1.length, t2Constructor.getParameters(), rs);
        return new Tuple2<>(
            instanciate(t1Constructor, values1),
            instanciate(t2Constructor, values2));
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T> findConstructor(Class<T> clazz, int parameters) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == parameters
                    && checkValidParameters(constructor.getParameterTypes())) {
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

    /**
     * It is only acceptable to store strong references to classes if they
     * come from the system classloader which hopefully won't be trashed
     * during the lifetime of the application.
     *
     * For that, we rely on {@link SqlBindings#supported(Class)} returning
     * false for non JRE classes.
     */
    private final Class<?>[] constructorParameterTypes;
    private final Reference<Class<T>> clazz;
    private Reference<Constructor<T>> constructor;

    private ClassMapping(Class<T> clazz) {
        this(findConstructor(clazz));
    }

    ClassMapping(Constructor<T> constructor) {
        this.constructorParameterTypes = constructor.getParameterTypes();
        this.clazz = new WeakReference<>(constructor.getDeclaringClass());
        // FIXME Using a SoftReference here is not optimal because it'll still
        // prevent the GC from retrieving the Class until free memory becomes
        // sparse enough to cause soft reference collection.
        this.constructor = new SoftReference<>(constructor);

        constructor.setAccessible(true);
    }

    protected Object[] getValues(int offset, Parameter[] parameters, ResultSet resultSet) throws SQLException {
        Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, i + offset + 1, parameters[i].getType());
        }
        return values;
    }

    private Constructor<T> getConstructor() {
        Constructor<T> constructor = this.constructor.get();
        if (constructor == null) {
            try {
                Class<T> clazz = this.clazz.get();
                if (clazz == null) {
                    throw new Error(
                        "You're requesting a constructor for a class that got gc'd, how did that happen?");
                }
                this.constructor = new SoftReference<>(constructor = clazz.getConstructor(constructorParameterTypes));
            } catch (NoSuchMethodException e) {
                throw new Error("Constructor for " + Arrays.toString(constructorParameterTypes) + " disappeared");
            }
        }
        return constructor;
    }

    @Override
    public T apply(ResultSet resultSet) throws SQLException {
        Constructor<T> constructor = getConstructor();
        return instanciate(constructor, getValues(0, constructor.getParameters(), resultSet));
    }

}
