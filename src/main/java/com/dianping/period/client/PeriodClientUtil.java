package com.dianping.period.client;

import com.dianping.period.common.PeriodEnv;

import java.util.List;

public class PeriodClientUtil {

    public static Object getProperty(String key) {
        return PeriodClientDataPool.get(key);
    }

    public static Object getProperty(String key, Object defaultValue) {
        Object zkData = PeriodClientDataPool.get(key);
        return (zkData == null) ? defaultValue : zkData;
    }

    public static Object getProperty(String key, String env) {
        PeriodEnv.isSupportedEnv(env);
        return PeriodClientDataPool.get(key, env);
    }

    public static Object getProperty(String key, Object defaultValue, String env) {
        PeriodEnv.isSupportedEnv(env);
        Object zkData = PeriodClientDataPool.get(key, env);
        return (zkData == null) ? defaultValue : zkData;
    }

    public static List<Object> getChildrenProperties(String fatherKey) {
        return PeriodClientDataPool.getChildren(fatherKey);
    }

    public static List<Object> getChildrenProperties(String fatherKey, String env) {
        PeriodEnv.isSupportedEnv(env);
        return PeriodClientDataPool.getChildren(fatherKey, env);
    }

}
