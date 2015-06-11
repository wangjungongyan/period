package com.period.client;

import com.period.common.PeriodEntity;

import java.util.Map;

/**
 * 1、获取一个节点的子节点，，不需要每次都去watch
 * 2、初始化不同环境的connect时，当需要获取哪个环境的connection时再初始化指定的connection
 * 3、客户端需要定时去刷新server端的节点数据
 * 4、client和server需要分开jar包
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
