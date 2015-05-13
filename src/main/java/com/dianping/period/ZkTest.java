package com.dianping.period;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkTest {

    static Integer mu = new Integer(-1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        Watcher wh = new Watcher() {

            public void process(WatchedEvent event) {
                synchronized (mu) {
                    System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型：" + event.getType());
                    mu.notify();
                }
            }
        };

        ZooKeeper zk = new ZooKeeper("172.16.238.128:2181", 500000, wh);

        while (true) {
            synchronized (mu) {
                List<String> firstChildRen = zk.getChildren("/root", true);
                if (firstChildRen != null) {
                    for (String firstChild : firstChildRen) {
                        zk.getChildren("/root/" + firstChild, true);
                    }
                }
                mu.wait();
            }

        }
    }
}
