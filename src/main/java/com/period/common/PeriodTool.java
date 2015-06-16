package com.period.common;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodTool {

    public static String ROOT_PATH = "/period";

    public static String FATHER = "father";

    public static String convertKey2Path(String originKey) {
        String[] splitedKeys = originKey.split("\\.");

        StringBuffer finalKey = new StringBuffer();

        for (String splitedKey : splitedKeys) {
            finalKey.append("/");
            finalKey.append(splitedKey);
        }

        return ROOT_PATH + finalKey.toString();
    }

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

}
