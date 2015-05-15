package com.dianping.period.common;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class PeriodConnection {

    public static ZooKeeper zk = null;

    static {
        try {
            zk = instanceZk();
        } catch (IOException e) {
            throw new RuntimeException("Instance ZooKeeper Object fail." + e);
        }
    }

    private static ZooKeeper instanceZk() throws IOException {
        return new ZooKeeper("172.16.238.128:2181", 500000, new PeriodWatcher());
    }
}
