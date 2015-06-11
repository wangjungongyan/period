package com.period.common;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by vali on 15-5-21.
 */
public class PeriodEnv {

    private static final String CURRENT_ENV_PATH = "/data/period/zk/currentEnv.properties";

    private static String currentEnv = initCurrentEnv();

    private static Set<String> supportedEnvs = new HashSet<String>();

    public static String getCurrentEnv() {
        return currentEnv;
    }

    public static void addSupportedEnv(Set<String> envs) {
        supportedEnvs.addAll(envs);
    }

    public static void isSupportedEnv(String env) {

        boolean isSupported = supportedEnvs.contains(env);

        if (!isSupported) {
            throw new IllegalArgumentException("Can not get data from env '" + env + "'.");
        }
    }

    private static String initCurrentEnv() {

        Properties prop = PeriodTool.getProperties(CURRENT_ENV_PATH);

        String currentEnv = (String) prop.get("currentEnv");

        if (currentEnv == null || "".equals(currentEnv)) {
            throw new IllegalArgumentException("Must set a not null currentEnv in path '" + CURRENT_ENV_PATH + "'.");
        }

        return currentEnv;
    }

}
