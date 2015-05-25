package com.dianping.period.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vali on 15-5-14.
 */
public class PeriodTool {

    public static String convertKey2Path(String originKey) {
        String[] splitedKeys = originKey.split("\\.");

        StringBuffer finalKey = new StringBuffer();

        for (String splitedKey : splitedKeys) {
            finalKey.append("/");
            finalKey.append(splitedKey);
        }

        return finalKey.toString();
    }

    public static String convertPath2Key(String path) {
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

}
