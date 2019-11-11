package net.intelie.tinymap.util;

public class DoubleCache {
    private final int smallCacheAmplitude;
    private final Double[] smallCache;
    private final CacheData<Double> data;

    public DoubleCache() {
        this((1 << 14), 4, 512);
    }

    public DoubleCache(int bucketCount, int bucketSize, int smallCacheAmplitude) {
        Preconditions.checkArgument(Integer.bitCount(bucketCount) == 1, "Bucket count must be power of two");
        this.smallCacheAmplitude = smallCacheAmplitude;
        this.data = new CacheData<>(bucketCount, bucketSize);
        this.smallCache = new Double[smallCacheAmplitude * 2];
        for (int i = 0; i < smallCache.length; i++) {
            smallCache[i] = (double) (i - smallCacheAmplitude);
        }
    }

    private static boolean eq(Double cached, double value) {
        return cached != null && cached == value;
    }

    public Double get(double value) {
        if (value >= -smallCacheAmplitude && value < smallCacheAmplitude && value == (int) value)
            return smallCache[(int) value + smallCacheAmplitude];
        int hash = Double.hashCode(value);
        int n = data.makeIndex(hash);
        Double cached = data.get(n);
        if (eq(cached, value))
            return cached;
        for (int k = 1; k < data.bucketSize(); k++)
            if (eq(cached = data.get(n + k), value))
                return data.finish(cached, n, k);
        return data.finish(value, n);
    }
}