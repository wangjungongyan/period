package com.dianping.period.client;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodTool;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class PeriodClientDataPool {

    private static ConcurrentHashMap<String, Object> pool = new ConcurrentHashMap<String, Object>();

    private static final Logger LOGGER = Logger.getLogger(PeriodClientDataPool.class);

    public static void add(String key, Object value) {
        pool.put(key, value);
    }

    public static void remove(String key) {
        pool.remove(key);
    }

    public static Object get(String key) {

        String path = PeriodTool.convertKey2Path(key);

        Object cacheData = pool.get(key);

        if (cacheData == null) {
            byte[] pathDataFromZk = null;
            try {
                pathDataFromZk = PeriodConnection.getZk().getData(path, true, null);
            } catch (Exception e) {
                LOGGER.error("Get data of path '" + path + "' fail.", e);
                return null;
            }

            cacheData = new String(pathDataFromZk);
            add(key, cacheData);
        }

        return cacheData;
    }

}
