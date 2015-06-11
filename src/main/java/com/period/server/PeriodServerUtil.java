package com.period.server;

import com.period.common.PeriodConnection;
import com.period.common.PeriodEntity;
import com.period.common.PeriodTool;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodServerUtil {

    private static final Logger LOGGER = Logger.getLogger(PeriodServerUtil.class);

    public static boolean updateNode(String key, String newData, String newDesc, String env) {

        PeriodEntity entity = new PeriodEntity(key, newData, newDesc);
        byte newDataBytes[] = PeriodTool.periodEntity2Json(entity).getBytes();
        int version = -1;
        String fullNodePath = PeriodTool.getFullNodePath(key);

        try {
            PeriodConnection.getClient(env).setData().withVersion(version).forPath(fullNodePath, newDataBytes);
        } catch (Exception e) {
            LOGGER.error("update new data '" + newData + "' to path '" + fullNodePath + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean deleteNode(String key, String env) {

        int version = -1;
        String fullNodePath = PeriodTool.getFullNodePath(key);

        try {
            PeriodConnection.getClient(env).delete().withVersion(version).forPath(fullNodePath);
        } catch (Exception e) {
            LOGGER.error("delete path '" + fullNodePath + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean createPersistentNode(String key, String data, String desc, String env) {
        return createNode(key, data, CreateMode.PERSISTENT, desc, env);
    }

    public static boolean createPersistentSequentialNode(String key, String data, String desc, String env) {
        return createNode(key, data, CreateMode.PERSISTENT_SEQUENTIAL, desc, env);
    }

    public static boolean createEphemeralNode(String key, String data, String desc, String env) {
        return createNode(key, data, CreateMode.EPHEMERAL, desc, env);
    }

    public static boolean createEphemeralSequentialNode(String key, String data, String desc, String env) {
        return createNode(key, data, CreateMode.EPHEMERAL_SEQUENTIAL, desc, env);
    }

    private static boolean createNode(String key, String data, CreateMode mode, String desc, String env) {

        String fullNodePath = PeriodTool.getFullNodePath(key);

        PeriodEntity entity = new PeriodEntity(key, data, desc);
        byte dataBytes[] = PeriodTool.periodEntity2Json(entity).getBytes();

        try {
            PeriodConnection.getClient(env).create().creatingParentsIfNeeded().withMode(mode).forPath(
                    fullNodePath,
                    dataBytes);
        } catch (Exception e) {
            LOGGER.error("create new path'" + fullNodePath + "',and set data to '" + data + "' fail.", e);
            return false;
        }

        return true;
    }

}
