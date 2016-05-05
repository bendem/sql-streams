package be.bendem.sqlstreams.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tuple2Tests {

    @Test
    public void testLeftRight() {
        Tuple2<Integer, String> tuple = new Tuple2<>(3, "hi");
        assertEquals(3, (int) tuple.left());
        assertEquals("hi", tuple.right());
    }

    @Test
    public void testMap() {
        Tuple2<Integer, String> tuple = new Tuple2<>(6, "hi");
        tuple = tuple.map(i -> i * 2, str -> str + " bob");
        assertEquals(12, (int) tuple.left);
        assertEquals("hi bob", tuple.right);

        assertEquals(6, (int) tuple.mapLeft(i -> i / 2).left);
        assertEquals("hi bob".length(), (int) tuple.mapRight(String::length).right);
    }

    @Test
    public void testStream() {
        assertEquals(Arrays.asList("hi", "bob"), Tuple2.stream(new Tuple2<>("hi", "bob")).collect(Collectors.toList()));
        assertEquals(Arrays.asList(3, 2f), Tuple2.stream(new Tuple2<>(3, 2f)).collect(Collectors.toList()));
    }

    @Test
    public void testToMap() {
        assertEquals(Collections.singletonMap("hi", "bob"), new Tuple2<>("hi", "bob").toMap());
    }

    @Test
    public void testSwap() {
        Tuple2<String, String> tuple = new Tuple2<>("hi", "bob").swap();
        assertEquals("bob", tuple.left);
        assertEquals("hi", tuple.right);
    }

    @Test
    public void testTransform() {
        assertEquals("hibob", new Tuple2<>("hi", "bob").transform(String::concat));
    }

    @Test
    public void testGrouping() {
        Map<Integer, List<String>> map =  Stream.of(
            new Tuple2<>(1, "bob"),
            new Tuple2<>(1, "georges"),
            new Tuple2<>(2, "paul"),
            new Tuple2<>(3, "laura"),
            new Tuple2<>(3, "sarah"))
            .collect(Tuple2.grouping());

        assertEquals(3, map.size());
        assertEquals(Arrays.asList("bob", "georges"), map.get(1));
        assertEquals(Arrays.asList("paul"), map.get(2));
        assertEquals(Arrays.asList("laura", "sarah"), map.get(3));
    }

    @Test
    public void testGroupingCustomCollections() {
        Map<Integer, List<String>> map = Stream.of(
            new Tuple2<>(1, "bob"),
            new Tuple2<>(1, "georges"),
            new Tuple2<>(2, "paul"),
            new Tuple2<>(3, "laura"),
            new Tuple2<>(3, "sarah"))
            .collect(Tuple2.grouping(TreeMap::new, CopyOnWriteArrayList::new));

        Assert.assertTrue(map instanceof TreeMap);
        map.values().forEach(list -> assertTrue(list instanceof CopyOnWriteArrayList));
    }

}
