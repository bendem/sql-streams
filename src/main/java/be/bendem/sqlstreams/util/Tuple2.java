package be.bendem.sqlstreams.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

// TODO Tests
public class Tuple2<Left, Right> {

    public static <Left, Right> Collector<Tuple2<Left, Right>, Map<Left, List<Right>>, Map<Left, List<Right>>> grouping() {
        return Collector.of(
            HashMap::new,
            (map, tuple) -> map.computeIfAbsent(tuple.left, left -> new ArrayList<>()).add(tuple.right),
            (left, right) -> {
                left.putAll(right);
                return left;
            });
    }

    public final Left left;
    public final Right right;

    public Tuple2(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public Left left() {
        return left;
    }

    public Right right() {
        return right;
    }

    public <RLeft, RRight> Tuple2<RLeft, RRight> map(Function<Left, RLeft> leftMapping, Function<Right, RRight> rightMapping) {
        return new Tuple2<>(leftMapping.apply(left), rightMapping.apply(right));
    }

    public <R> Tuple2<R, Right> mapLeft(Function<Left, R> mapping) {
        return new Tuple2<>(mapping.apply(left), right);
    }

    public <R> Tuple2<Left, R> mapRight(Function<Right, R> mapping) {
        return new Tuple2<>(left, mapping.apply(right));
    }

    public Tuple2<Right, Left> swap() {
        return new Tuple2<>(right, left);
    }

    public Map<Left, Right> toMap() {
        return toMap(HashMap::new);
    }

    public Map<Left, Right> toMap(Supplier<Map<Left, Right>> mapSupplier) {
        Map<Left, Right> map = mapSupplier.get();
        map.put(left, right);
        return map;
    }

    public <T> T transform(BiFunction<Left, Right, T> mapping) {
        return mapping.apply(left, right);
    }

}
