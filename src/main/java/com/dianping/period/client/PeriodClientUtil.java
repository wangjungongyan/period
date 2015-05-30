package com.dianping.period.client;

import java.util.Map;

public class PeriodClientUtil {

    public static Object getProperty(String key) {
        return PeriodClientDataPool.get(key);
    }

    public static Object getProperty(String key, Object defaultValue) {
        Object zkData = PeriodClientDataPool.get(key);
        return (zkData == null) ? defaultValue : zkData;
    }

    public static Object getProperty(String key, String env) {
        return PeriodClientDataPool.get(key, env);
    }

    public static Object getProperty(String key, Object defaultValue, String env) {
        Object zkData = PeriodClientDataPool.get(key, env);
        return (zkData == null) ? defaultValue : zkData;
    }

    public static Map<String, String> getChildrenProperties(String fatherKey) {
        return PeriodClientDataPool.getChildren(fatherKey);
    }

    public static Map<String, String> getChildrenProperties(String fatherKey, String env) {
        return PeriodClientDataPool.getChildren(fatherKey, env);
    }

}
