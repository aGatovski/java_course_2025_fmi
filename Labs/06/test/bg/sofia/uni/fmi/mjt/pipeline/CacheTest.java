package bg.sofia.uni.fmi.mjt.pipeline;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {
    private Cache cut = new Cache();

    @Test
    void testCacheValueThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cut.cacheValue(null, null), "Key is null");
    }

    @Test
    void testCacheValueThrowsIllegalArgumentExceptionWhenValueIsNull() {
        Object dummyValue = new Object();

        assertThrows(IllegalArgumentException.class, () -> cut.cacheValue(dummyValue, null), "Key is null");
    }

    @Test
    void testCacheValueStore() {
        String key = "validKey";
        String value = "validValue";

        cut.cacheValue(key, value);

        assertTrue(cut.containsKey(key),"The cache should contain the key!");
    }

    @Test
    void testGetCacheValueThrowsIllegalArgumentExceptionWhenKeyIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cut.getCachedValue(null), "Key is null");
    }

    @Test
    void testGetCachedValueReturnsNullWhenKeyDoesNotExist() {
        String key  = "NoKey";

        assertNull(cut.getCachedValue(key),
            "Key is not in cache!");
    }

    @Test
    void testCacheIsEmptyAfterClear(){
        cut.cacheValue("key1", "value1");
        cut.cacheValue("key2", "value2");

        cut.clear();

        assertTrue(cut.isEmpty(),"Cache is empty after clear!");
    }

}