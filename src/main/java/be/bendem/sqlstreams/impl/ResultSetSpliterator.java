package be.bendem.sqlstreams.impl;

import be.bendem.sqlstreams.util.SqlFunction;
import be.bendem.sqlstreams.util.Wrap;

import java.sql.ResultSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

    static <T> Stream<T> stream(SqlFunction<ResultSet, T> mapping, ResultSet resultSet) {
        return StreamSupport
            .stream(new ResultSetSpliterator<>(mapping, resultSet), false)
            .onClose(() -> Wrap.execute(resultSet::close));
    }

    private final SqlFunction<ResultSet, T> mapping;
    private final ResultSet resultSet;

    private ResultSetSpliterator(SqlFunction<ResultSet, T> mapping, ResultSet resultSet) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.resultSet = resultSet;
        this.mapping = mapping;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        return Wrap.get(() -> {
            boolean hasNext;
            if (hasNext = resultSet.next()) {
                consumer.accept(mapping.apply(resultSet));
            }
            return hasNext;
        });
    }
}
