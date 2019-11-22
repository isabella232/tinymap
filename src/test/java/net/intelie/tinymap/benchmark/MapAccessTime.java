package net.intelie.tinymap.benchmark;

import com.google.common.collect.ImmutableMap;
import net.intelie.introspective.ThreadResources;
import net.intelie.tinymap.MutableTinyMap;
import net.intelie.tinymap.TestSizeUtils;
import net.intelie.tinymap.TinyMap;
import net.intelie.tinymap.TinyMapBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Ignore
public class MapAccessTime {
    @Test
    public void main() {
        ImmutableMap.Builder<String, Object> guava = ImmutableMap.builder();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> linked = new LinkedHashMap<>();
        TinyMapBuilder<String, Object> tiny = TinyMap.builder();
        MutableTinyMap<String, Object> mutable = new MutableTinyMap<>();

        String[] keys = new String[100];

        Random random = new Random();

        for (int i = 0; i < keys.length; i++) {
            keys[i] = "key" + random.nextInt();
            map.put(keys[i], "value" + i);
            linked.put(keys[i], "value" + i);
            guava.put(keys[i], "value" + i);
            tiny.put(keys[i], "value" + i);
            mutable.put(keys[i], "value" + i);
        }

        test("Tiny", keys, tiny.build());
        test("MutableTiny", keys, mutable);
        test("LinkedHashMap", keys, linked);
        test("HashMap", keys, map);
        test("Guava", keys, guava.build());
    }

    private void test(String name, String[] keys, Map<String, Object> map) {
        for (int i = 0; i < 100000; i++)
            for (String key : keys)
                map.get(key);


        long startTime = System.nanoTime();
        long startMem = ThreadResources.allocatedBytes();
        for (int i = 0; i < 2000000; i++)
            for (String key : keys)
                map.get(key);

        long endMem = ThreadResources.allocatedBytes() - startMem;
        long endTime = System.nanoTime() - startTime;

        System.out.println(name);
        System.out.println("  alloc: " + TestSizeUtils.formatBytes(endMem));
        System.out.println("  time: " + endTime / 1e9);

        if (map instanceof TinyMap) {
            long total = 0;
            for (String key : keys) {
                total += ((TinyMap<String, Object>) map).debugCollisions(key);
            }
            System.out.println("  collisions: " + (total / (double) keys.length));
        }
    }
}