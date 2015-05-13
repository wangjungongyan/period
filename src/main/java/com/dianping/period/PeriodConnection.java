package com.dianping.period;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

public class PeriodConnection {

    public static ZooKeeper zk = null;

    public static void instanceZk() throws IOException {
        zk = new ZooKeeper("172.16.238.128:2181", 500000, new PeriodWatcher());
    }
}
