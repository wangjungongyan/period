package com.dianping.period;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class PeriodWatcher implements Watcher {

    public void process(WatchedEvent event) {
        synchronized (PeriodSemaphore.MUTX) {
            System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型：" + event.getType());

            EventType eventType = event.getType();
            String path = event.getPath();

            List<String> children;
            try {
                ZooKeeper zk = PeriodConnection.zk;
                children = zk.getChildren("/period", true);
                for (String child : children) {
                    zk.getChildren("/period/" + child, true);
                    byte[] data = zk.getData("/period/" + child, true, null);
                    String s = new String(data);
                    System.out.println();
                }

            } catch (KeeperException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (eventType == EventType.NodeCreated) {

            }

            if (eventType == EventType.NodeDeleted) {
                PeriodDataPool.remove("");
            }

            if (eventType == EventType.NodeChildrenChanged) {

            }

            if (eventType == EventType.NodeDataChanged) {

            }
        }

    }

}
