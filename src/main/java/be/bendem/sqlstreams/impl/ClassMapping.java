package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.MappingConstructor;
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
import java.util.*;
import java.util.stream.Collectors;

class ClassMapping<T> implements SqlFunction<ResultSet, T> {

    private static final Map<Class<?>, ClassMapping<?>> MAPPINGS = Collections.synchronizedMap(new WeakHashMap<>());

    @SuppressWarnings("unchecked")
    public static <T> ClassMapping<T> get(Class<T> clazz) {
        return (ClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ClassMapping<>(clazz));
    }

    private static <T> T instantiate(Constructor<T> constructor, Object[] values) {
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
            instantiate(t1Constructor, values1),
            instantiate(t2Constructor, values2));
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
        this(Constructors.findConstructor(clazz));
    }

    ClassMapping(Constructor<T> constructor) {
        this.constructorParameterTypes = constructor.getParameterTypes();
        this.clazz = new WeakReference<>(constructor.getDeclaringClass());
        // NOTE Using a SoftReference here is not optimal because it'll still
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
        return instantiate(constructor, getValues(0, constructor.getParameters(), resultSet));
    }

    static class Constructors {
        @SuppressWarnings("unchecked")
        static <T> Constructor<T> findConstructor(Class<T> clazz, int parameters) {
            List<Constructor<?>> constructors = Arrays.stream(clazz.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == parameters)
                .collect(Collectors.toList());

            if (constructors.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                    "No constructor for '%s' with exactly %d parameters", clazz, parameters));
            }

            if (constructors.size() != 1) {
                throw new IllegalArgumentException(String.format(
                    "Too many constructors for '%s' with exactly %d parameters (%s)",
                    clazz, parameters, join(constructors)));
            }

            return checkValidParameters((Constructor<T>) constructors.iterator().next());
        }

        @SuppressWarnings("unchecked")
        private static <T> Constructor<T> findConstructor(Class<T> clazz) {
            Constructor<?>[] constructors = clazz.getConstructors();

            if (constructors.length == 0) {
                throw new IllegalArgumentException("No constructor for " + clazz);
            }

            if (constructors.length == 1) {
                return checkValidParameters((Constructor<T>) constructors[0]);
            }

            Constructor<?> found = null;
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(MappingConstructor.class)) {
                    if (found != null) {
                        throw new IllegalArgumentException(String.format(
                            "Multiple constructors for '%s' marked with '%s' (at least '%s' and '%s')",
                            clazz,  MappingConstructor.class, found, constructor));
                    }
                    found = constructor;
                }
            }

            if (found == null) {
                throw new IllegalArgumentException(String.format(
                    "Too many constructors found for %s. Mark one with @%s",
                    clazz, MappingConstructor.class.getSimpleName()));
            }

            return checkValidParameters((Constructor<T>) found);
        }

        private static <T> Constructor<T> checkValidParameters(Constructor<T> constructor) {
            Set<? extends Class<?>> invalid = Arrays.stream(constructor.getParameterTypes())
                .distinct()
                .filter(c -> !SqlBindings.supported(c))
                .collect(Collectors.toSet());
            if (!invalid.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                    "Unsupported parameters for constructor '%s' (%s).",
                    constructor, join(invalid)));
            }

            return constructor;
        }

        private static <T> String join(Collection<T> collection) {
            return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        }
    }

}
