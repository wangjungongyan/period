package com.dianping.period.server;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodEnv;
import com.dianping.period.common.PeriodTool;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodServerUtil {

    private static final Logger LOGGER = Logger.getLogger(PeriodServerUtil.class);

    public static boolean updateNode(String key, String newData, String env) {

        PeriodEnv.isSupportedEnv(env);

        byte newDataBytes[] = (newData != null) ? newData.getBytes() : null;
        String path = PeriodTool.convertKey2Path(key);
        int version = -1;

        try {
            PeriodConnection.getClient(env).setData().withVersion(version).forPath(path, newDataBytes);
        } catch (Exception e) {
            LOGGER.error("update new data '" + newData + "' to path '" + path + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean deleteNode(String key, String env) {

        PeriodEnv.isSupportedEnv(env);

        String path = PeriodTool.convertKey2Path(key);
        int version = -1;

        try {
            PeriodConnection.getClient(env).delete().withVersion(version).forPath(path);
        } catch (Exception e) {
            LOGGER.error("delete path '" + path + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean createPersistentNode(String key, String data, String env) {
        return createNode(key, data, CreateMode.PERSISTENT, env);
    }

    public static boolean createPersistentSequentialNode(String key, String data, String env) {
        return createNode(key, data, CreateMode.PERSISTENT_SEQUENTIAL, env);
    }

    public static boolean createEphemeralNode(String key, String data, String env) {
        return createNode(key, data, CreateMode.EPHEMERAL, env);
    }

    public static boolean createEphemeralSequentialNode(String key, String data, String env) {
        return createNode(key, data, CreateMode.EPHEMERAL_SEQUENTIAL, env);
    }

    private static boolean createNode(String key, String data, CreateMode mode, String env) {

        PeriodEnv.isSupportedEnv(env);

        byte dataBytes[] = (data != null) ? data.getBytes() : null;
        String path = PeriodTool.convertKey2Path(key);

        try {
            PeriodConnection.getClient(env).create().withMode(mode).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path,
                                                                                                                 dataBytes);
        } catch (Exception e) {
            LOGGER.error("create new path'" + path + "',and set data to '" + data + "' fail.", e);
            return false;
        }

        return true;
    }

}
