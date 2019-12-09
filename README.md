# TinyMap

Memory-Efficient Immutable HashMap

This library provides a straightforward open-addressing ordered hash table implementation. That implementation, along
with an aggressive caching strategy (also provided here) can lead to incredibly low memory usage for semi-structured 
hashmaps.

That is very useful to represent small immutable events. 

The main advantage in TinyMap is that you can cache not only keys and values, but also entire maps, keysets, and lists. This can lead to a representation up to 97% smaller than a typical `HashMap`.

Below you can compare the memory requirements of loading 50K events as Gson's LinkedTreeMap, converting them to LinkedHashMap, guava's ImmutableMap (with and without strings and doubles cache), and using TinyMap caching everything (even map keys).

![](https://docs.google.com/spreadsheets/d/e/2PACX-1vQGaL2vuiOAxMH8809j4HiYPfK1uxSYpNIYNQAl-_eGbvhBC2BJR2bE_-sbAhBkq-xFpTzTa3hcUZ9i/pubchart?oid=1134324197&format=image)

### Are there any downsides?

Yes. We save some memory by not storing Entry<K, V> objects, only arrays of keys and values. 
So, iterating through the map's `entrySet` performs more allocations than a typical Map implementation.

This can be mitigated by using `Map#forEach` or using `getKeyAt(index)` and `getValueAt(index)` methods in a for loop.

## Usage

TinyMap is available through Maven Central repository, just add the following
dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>net.intelie.tinymap</groupId>
    <artifactId>tinymap</artifactId>
    <version>0.6</version>
</dependency>
```

### Building a new map (without caches)

```java
TinyMap<Object, Object> built = TinyMap.builder()
        .put("key1", "value1")
        .put("key2", TinyList.builder().add(42.0).add("subvalue").build())
        .build();
```

This map uses exactly 384 bytes in Java 8, considering all its object tree. This is already better than 
Guava's ImmutableMap (408 bytes) and LinkedHashMap (528 bytes).

### Optimizing existing map (with cache)

TinyMap can leverage aggressive caching to avoid representing same maps, keySets, or even Strings multiple times.

```java
ArrayList<Object> list = new ArrayList<>();

for (int i = 0; i < 1000; i++) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    map.put("key1", "value" + i);
    map.put("key2", i);
    map.put("key3", (double)(i/100));
    list.add(map);
}

ObjectOptimizer optimizer = new ObjectOptimizer(new ObjectCache());
TinyList<Object> tinyList = optimizer.optimizeList(list);
```

The optimized version uses almost 60% less memory than the pure Java version (137.19 KB vs 348.75 KB).

### Parsing JSON

TODO 
