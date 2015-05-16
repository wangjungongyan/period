package com.dianping.period.common;

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

}
