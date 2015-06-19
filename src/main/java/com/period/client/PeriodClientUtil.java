package com.period.client;

import com.period.common.PeriodEntity;

import java.util.Map;

/**
 * 1、获取一个节点的子节点，，不需要每次都去watch
 * 2、client和server需要分开jar包
 */
public class PeriodClientUtil {

    public static PeriodEntity getProperty(String key) {
        return PeriodClientDataPool.get(key);
    }

    public static Map<String, PeriodEntity> getChildrenProperties(String fatherKey) {
        return PeriodClientDataPool.getChildren(fatherKey);
    }

    public static Map<String, PeriodEntity> getChildrenProperties(String fatherKey, String env) {
        return PeriodClientDataPool.getChildren(fatherKey, env);
    }

}
