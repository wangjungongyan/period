package com.period.client;

import com.alibaba.fastjson.JSON;
import com.period.common.PeriodEntity;
import com.period.common.PeriodTool;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vali on 15-6-16.
 */
public class PeriodClientMonitor {

    private static Thread daemonInstance = getDaemonInstance();

    private static final Logger LOGGER = Logger.getLogger(PeriodClientMonitor.class);

    public static void startClientDaemon() {
        daemonInstance.start();
    }

    private static Thread getDaemonInstance() {

        Thread thread = new Thread() {

            @Override public void run() {

                while (true) {

                    LOGGER.info("Start monitor...");

                    ConcurrentHashMap pool = PeriodClientDataPool.getPool();

                    Set keys = pool.keySet();

                    if (keys.size() == 0) {
                        LOGGER.info("End monitor.Because of key size is 0.");
                        sleepOneMinute();
                        continue;
                    }

                    Iterator it = keys.iterator();
                    while (it.hasNext()) {

                        String key = (String) it.next();
                        String env = getEnv(key);
                        boolean isFatherKey = isFatherKey(env, key);

                        if (isFatherKey) {
                            String fatherKey = getFatherKeyExcludeEnv(env, key);
                            Map<String, PeriodEntity> childrenData = PeriodTool.getChildrebData(fatherKey, env);
                            PeriodClientDataPool.addOrCoverLocalCache(PeriodTool.FATHER + "_" + fatherKey, childrenData,
                                                                      env);
                            LOGGER.info("Monitor key '" + key
                                        + "',and the new value is "
                                        + JSON.toJSONString(
                                    childrenData));
                        } else {
                            PeriodEntity data = PeriodTool.getData(getKeyExcludeEnv(env, key), env);
                            PeriodClientDataPool.addOrCoverLocalCache(data, env);
                            LOGGER.info("Monitor key '" + key + "',and the new value is " + JSON.toJSONString(data));
                        }
                    }

                    LOGGER.info("End monitor. Monitored keys are : " + JSON.toJSONString(keys));
                    sleepOneMinute();
                }
            }
        };

        thread.setDaemon(true);
        return thread;

    }

    private static void sleepOneMinute() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFatherKey(String env, String key) {
        return key.startsWith(env + "_" + PeriodTool.FATHER);
    }

    private static String getEnv(String key) {
        return key.split("\\_")[0];
    }

    private static String getKeyExcludeEnv(String env, String key) {
        int index = (env + "_").length();
        return key.substring(index);
    }

    private static String getFatherKeyExcludeEnv(String env, String key) {
        int index = (env + "_" + PeriodTool.FATHER).length() + 1;
        return key.substring(index);
    }

}
