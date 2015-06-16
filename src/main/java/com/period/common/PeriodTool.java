package com.period.common;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodTool {

    public static String ROOT_PATH = "/period";

    public static String FATHER = "father";

    private static final Logger LOGGER = Logger.getLogger(PeriodTool.class);

    public static String convertPath2Key(String path) {
        if (path.startsWith(ROOT_PATH)) {
            String subPath = path.substring(ROOT_PATH.length() + 1);
            return subPath.replaceAll("/", ".");
        }

        String subPath = path.substring(1);
        return subPath.replaceAll("/", ".");
    }

    public static Properties getProperties(String path) {
        Properties prop = new Properties();

        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
            prop.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Period init env properties error :" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                in = null;
            }
        }

        return prop;
    }

    public static String getFullNodePath(String key) {

        if (key.indexOf(".") == -1) {
            return ROOT_PATH + "/" + key;
        }

        String projectName = key.substring(0, key.indexOf("."));
        String subNode = key.substring(key.indexOf(".") + 1);
        String fullNodePath = ROOT_PATH + "/" + projectName + "/" + subNode;
        return fullNodePath;
    }

    public static PeriodEntity convertJson2Entity(String jsonBody) {

        if (jsonBody == null || "".equals(jsonBody)) {
            return null;
        }

        return JSON.parseObject(jsonBody, PeriodEntity.class);
    }

    public static String convertEntity2Json(PeriodEntity entity) {

        if (entity == null) {
            return null;
        }

        return JSON.toJSONString(entity);
    }

    public static String getFatherKey(String key, String env) {
        return env + "_" + PeriodTool.FATHER + "_" + key;
    }

    public static Map<String, PeriodEntity> getChildrebData(String fatherKey, String env) {

        String fatherPath = PeriodTool.getFullNodePath(fatherKey);

        CuratorFramework client = PeriodConnection.getClient(env);

        try {
            List<String> childrenPaths = client.getChildren().watched().forPath(fatherPath);

            if (childrenPaths == null || childrenPaths.size() == 0) return null;

            Map<String, PeriodEntity> childrenData = new HashMap<String, PeriodEntity>();

            for (String childPath : childrenPaths) {
                String childFullPath = fatherPath + "/" + childPath;
                String childFullKey = fatherKey + "." + childPath;
                byte[] childData = client.getData().watched().forPath(childFullPath);

                LOGGER.info("childFullKey:" + childFullKey + ",childPath:" + childPath + ",value:" + new String(
                        childData, Charsets.UTF_8));

                childrenData.put(childFullKey, PeriodTool.convertJson2Entity(new String(childData, Charsets.UTF_8)));
            }

            return childrenData;

        } catch (Exception e) {
            LOGGER.error("Get data of father path '" + fatherKey + "' fail.", e);
            return null;
        }

    }

    public static PeriodEntity getData(String key, String env) {

        String fullNodePath = PeriodTool.getFullNodePath(key);

        try {
            byte[] pathDataFromZk = PeriodConnection.getClient(env).getData().watched().forPath(
                    fullNodePath);

            return PeriodTool.convertJson2Entity(new String(pathDataFromZk, Charsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("Get data of path '" + fullNodePath + "' fail.", e);
            return null;
        }
    }

}
