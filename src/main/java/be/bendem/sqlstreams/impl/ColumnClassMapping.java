package be.bendem.sqlstreams.impl;

import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ColumnClassMapping<T> extends ClassMapping<T> {

    private static final Map<Class<?>, ColumnClassMapping<?>> MAPPINGS = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ColumnClassMapping<T> get(Class<T> clazz, int[] columns) {
        return (ColumnClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ColumnClassMapping<>(clazz, columns));
    }

    private final int[] columns;

    private ColumnClassMapping(Class<T> clazz, int[] columns) {
        super(findConstructor(clazz, columns.length));
        this.columns = columns;
    }

    @Override
    protected Object[] getValues(Parameter[] parameters, ResultSet resultSet) throws SQLException {
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < columns.length; i++) {
            values[i] = SqlBindings.map(resultSet, columns[i], parameters[i].getType());
        }
        return values;
    }
}
