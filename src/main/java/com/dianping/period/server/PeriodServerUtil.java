package com.dianping.period.server;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodTool;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodServerUtil {

    private static final Logger LOGGER = Logger.getLogger(PeriodServerUtil.class);

    public static boolean updateNode(String key, String newData) {

        byte newDataBytes[] = (newData != null) ? newData.getBytes() : null;
        String path = PeriodTool.convertKey2Path(key);
        int version = -1;

        try {
            PeriodConnection.zk.setData(path, newDataBytes, version);
        } catch (Exception e) {
            LOGGER.error("update new data '" + newData + "' to path '" + path + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean deleteNode(String key) {

        String path = PeriodTool.convertKey2Path(key);
        int version = -1;

        try {
            PeriodConnection.zk.delete(path, version);
        } catch (Exception e) {
            LOGGER.error("delete path '" + path + "' fail.", e);
            return false;
        }

        return true;
    }

    public static boolean createPersistentNode(String key, String data) {
        return createNode(key, data, CreateMode.PERSISTENT);
    }

    public static boolean createPersistentSequentialNode(String key, String data) {
        return createNode(key, data, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    public static boolean createEphemeralNode(String key, String data) {
        return createNode(key, data, CreateMode.EPHEMERAL);
    }

    public static boolean createEphemeralSequentialNode(String key, String data) {
        return createNode(key, data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    private static boolean createNode(String key, String data, CreateMode mode) {
        byte dataBytes[] = (data != null) ? data.getBytes() : null;
        String path = PeriodTool.convertKey2Path(key);

        try {
            PeriodConnection.zk.create(path, dataBytes, null, mode);
        } catch (Exception e) {
            LOGGER.error("create new path'" + path + "',and set data to '" + data + "' fail.", e);
            return false;
        }

        return true;
    }

}
