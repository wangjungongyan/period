package com.dianping.period.client;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodEnv;
import com.dianping.period.common.PeriodTool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodClientDataPool {

    private static ConcurrentHashMap<String, Object> pool = new ConcurrentHashMap<String, Object>();

    private static final Logger LOGGER = Logger.getLogger(PeriodClientDataPool.class);

    public static void add(String key, Object value) {
        add(key, value, PeriodEnv.getCurrentEnv());
    }

    public static void add(String key, Object value, String env) {
        pool.put(env + "_" + key, value);
    }

    public static void remove(String key) {
        remove(key, PeriodEnv.getCurrentEnv());
    }

    public static void remove(String key, String env) {
        pool.remove(env + "_" + key);
    }

    public static Object get(String key) {
        return get(key, PeriodEnv.getCurrentEnv());
    }

    public static Object get(String key, String env) {

        String path = PeriodTool.convertKey2Path(key);

        String cacheKey = env + "_" + key;

        Object cacheData = pool.get(cacheKey);

        if (cacheData == null) {
            byte[] pathDataFromZk = null;
            try {
                pathDataFromZk = PeriodConnection.getClient(env).getData().watched().forPath(
                        path);
            } catch (Exception e) {
                LOGGER.error("Get data of path '" + path + "' fail.", e);
                return null;
            }

            cacheData = new String(pathDataFromZk);
            add(key, cacheData, env);
        }

        return cacheData;
    }

    public static Map<String, String> getChildren(String fatherKey) {
        return getChildren(fatherKey, PeriodEnv.getCurrentEnv());
    }

    public static Map<String, String> getChildren(String fatherKey, String env) {

        Map<String, String> childrenData = new HashMap<String, String>();

        String fatherPath = PeriodTool.convertKey2Path(fatherKey);

        CuratorFramework client = PeriodConnection.getClient(env);

        try {
            client.getData().watched().forPath(fatherPath);

            List<String> childrenkeys = client.getChildren().watched().forPath(fatherPath);

            if (childrenkeys == null || childrenkeys.size() == 0) return new HashMap<String, String>();

            for (String childkey : childrenkeys) {
                String childPath = fatherPath + "/" + childkey;
                String childKey = PeriodTool.convertPath2Key(childPath);

                Object cacheData = pool.get(env + "_" + PeriodTool.convertPath2Key(childPath));

                if (cacheData == null) {
                    byte[] childData = client.getData().watched().forPath(childPath);
                    childrenData.put(childKey, new String(childData));
                    add(childKey, new String(childData), env);
                    continue;
                }

                childrenData.put(childKey, cacheData.toString());
            }

        } catch (Exception e) {
            LOGGER.error("Get data of father path '" + fatherKey + "' fail.", e);
            return null;
        }

        return childrenData;
    }

}
