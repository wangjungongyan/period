package com.dianping.period.common;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class PeriodConnection {

    private static ZooKeeper zk = null;

    private static String ZK_ENV_PATH = "/data/period/zk/env.properties";

    static {
        try {
            zk = instanceZk();
        } catch (IOException e) {
            throw new RuntimeException("Instance ZooKeeper Object fail." + e);
        }
    }

    public static ZooKeeper getZk() {
        return zk;
    }

    private static ZooKeeper instanceZk() throws IOException {
//        Properties prop = new Properties();
//
//        InputStream in = null;
//        try {
//            in = new FileInputStream(new File(ZK_ENV_PATH));
//            prop.load(in);
//        } catch (Exception e) {
//            throw new RuntimeException("init env properties error :" + e.getMessage());
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                in = null;
//            }
//        }
//
//        String cluster = (String) prop.get("cluster");

          return new ZooKeeper("172.16.238.128:2181", 500000, new PeriodWatcher());
    }
}
