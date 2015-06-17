package com.period.client;

import com.period.common.PeriodEntity;
import com.period.common.PeriodEnv;
import com.period.common.PeriodTool;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodClientDataPool {

    private static ConcurrentHashMap<String, Object> pool = new ConcurrentHashMap<String, Object>();

    private static final Logger LOGGER = Logger.getLogger(PeriodClientDataPool.class);

    public static Object getLocalCache(String key) {
        return pool.get(key);
    }

    public static void addOrCoverLocalCache(PeriodEntity entity, String env) {
        if (entity == null) return;
        pool.put(env + "_" + entity.getKey(), entity);
    }

    public static void addOrCoverLocalCache(String key, Object value, String env) {
        if (value == null) return;
        pool.put(env + "_" + key, value);
    }

    public static void removeLocalCache(String key, String env) {
        pool.remove(env + "_" + key);
    }

    public static ConcurrentHashMap getPool() {
        return pool;
    }

    public static PeriodEntity get(String key) {
        return get(key, PeriodEnv.getCurrentEnv());
    }

    public static PeriodEntity get(String key, String env) {

        String cacheKey = env + "_" + key;

        PeriodEntity cacheData = (PeriodEntity) pool.get(cacheKey);

        if (cacheData == null) {
            cacheData = PeriodTool.getData(key, env);
            addOrCoverLocalCache(cacheData, env);
        }

        return cacheData;
    }

    public static Map<String, PeriodEntity> getChildren(String fatherKey) {
        return getChildren(fatherKey, PeriodEnv.getCurrentEnv());
    }

    public static Map<String, PeriodEntity> getChildren(String fatherKey, String env) {

        String cacheKey = PeriodTool.getFatherKey(fatherKey, env);

        Map<String, PeriodEntity> cacheData = (Map<String, PeriodEntity>) (pool.get(cacheKey));

        if (cacheData == null) {

            try {

                cacheData = PeriodTool.getChildrenData(fatherKey, env);

                addOrCoverLocalCache(PeriodTool.FATHER + "_" + fatherKey, cacheData, env);

            } catch (Exception e) {
                LOGGER.error("Get data of father path '" + fatherKey + "' fail.", e);
                return null;
            }
        }

        return cacheData;
    }

}
