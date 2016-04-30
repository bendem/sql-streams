package be.bendem.sqlstreams.impl;

import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ColumnNamesClassMapping<T> extends ClassMapping<T> {

    private static final Map<Class<?>, ColumnNamesClassMapping<?>> MAPPINGS = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> ColumnNamesClassMapping<T> get(Class<T> clazz, String[] columnNames) {
        return (ColumnNamesClassMapping<T>) MAPPINGS.computeIfAbsent(clazz, c -> new ColumnNamesClassMapping<>(clazz, columnNames));
    }

    private final String[] columnNames;

    @SuppressWarnings("unchecked")
    private ColumnNamesClassMapping(Class<T> clazz, String[] columnNames) {
        super(findConstructor(clazz, columnNames.length));
        this.columnNames = columnNames;
    }

    @Override
    protected Object[] getValues(Parameter[] parameters, ResultSet resultSet) throws SQLException {
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            values[i] = SqlBindings.map(resultSet, columnNames[i], parameters[i].getType());
        }
        return values;
    }

}
