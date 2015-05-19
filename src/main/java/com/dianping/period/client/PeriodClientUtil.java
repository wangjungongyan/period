package com.dianping.period.client;

public class PeriodClientUtil {

    public static Object getProperty(String key) {
        return PeriodClientDataPool.get(key);
    }

    public static Object getProperty(String key, Object defaultValue) {
        Object zkData = PeriodClientDataPool.get(key);
        return (zkData == null) ? defaultValue : zkData;
    }

}
