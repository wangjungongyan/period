package com.dianping.period;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodDataPool {

    private static Map<String, Object> pool = new ConcurrentHashMap<String, Object>();

    public static void add(String key, Object value) {
        pool.put(key, value);
    }

    public static void remove(String key) {
        pool.remove(key);
    }

    public static void update(String key, Object newValue) {
        pool.put(key, newValue);
    }

    public static Object get(String key) {
        return pool.get(key);
    }

}
