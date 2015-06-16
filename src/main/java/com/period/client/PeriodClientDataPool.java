package com.period.client;

import com.google.common.base.Charsets;
import com.period.common.PeriodConnection;
import com.period.common.PeriodEntity;
import com.period.common.PeriodEnv;
import com.period.common.PeriodTool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodClientDataPool {

    private static ConcurrentHashMap<String, Object> pool = new ConcurrentHashMap<String, Object>();

    private static final Logger LOGGER = Logger.getLogger(PeriodClientDataPool.class);

    public static Object getLocalCache(String key){
        return pool.get(key);
    }

    public static void addLocalCache(PeriodEntity entity, String env) {
        pool.put(env + "_" + entity.getKey(), entity);
    }

    public static void addLocalCache(String key, Object value, String env) {
        pool.put(env + "_" + key, value);
    }

    public static void removeLocalCache(String key, String env) {
        pool.remove(env + "_" + key);
    }

    public static PeriodEntity get(String key) {
        return get(key, PeriodEnv.getCurrentEnv());
    }

    public static PeriodEntity get(String key, String env) {

        String fullNodePath = PeriodTool.getFullNodePath(key);

        String cacheKey = env + "_" + key;

        PeriodEntity cacheData = (PeriodEntity) pool.get(cacheKey);

        if (cacheData == null) {
            byte[] pathDataFromZk = null;
            try {
                pathDataFromZk = PeriodConnection.getClient(env).getData().watched().forPath(
                        fullNodePath);
                cacheData = PeriodTool.convertJson2Entity(new String(pathDataFromZk, Charsets.UTF_8));
            } catch (Exception e) {
                LOGGER.error("Get data of path '" + fullNodePath + "' fail.", e);
                return null;
            }

            addLocalCache(cacheData, env);
        }

        return cacheData;
    }

    public static Map<String, PeriodEntity> getChildren(String fatherKey) {
        return getChildren(fatherKey, PeriodEnv.getCurrentEnv());
    }

    public static Map<String, PeriodEntity> getChildren(String fatherKey, String env) {

        Map<String, PeriodEntity> childrenData = new HashMap<String, PeriodEntity>();

        String cacheKey = env + "_" + PeriodTool.FATHER + "_" + fatherKey;

        Map<String, PeriodEntity> cacheData = (Map<String, PeriodEntity>) (pool.get(cacheKey));

        if (cacheData == null) {

            String fatherPath = PeriodTool.getFullNodePath(fatherKey);
            CuratorFramework client = PeriodConnection.getClient(env);

            try {

                List<String> childrenPaths = client.getChildren().watched().forPath(fatherPath);

                if (childrenPaths == null || childrenPaths.size() == 0) return null;

                for (String childPath : childrenPaths) {
                    String childFullPath = fatherPath + "/" + childPath;
                    String childFullKey = fatherKey + "." + childPath;
                    byte[] childData = client.getData().watched().forPath(childFullPath);
                    childrenData.put(childFullKey, PeriodTool.convertJson2Entity(new String(childData, Charsets.UTF_8)));
                }

                addLocalCache(PeriodTool.FATHER + "_" + fatherKey, childrenData, env);

                return childrenData;

            } catch (Exception e) {
                LOGGER.error("Get data of father path '" + fatherKey + "' fail.", e);
                return null;
            }
        }

        return cacheData;
    }

}
