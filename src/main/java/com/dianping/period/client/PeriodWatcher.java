package com.dianping.period.client;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodTool;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * 1、TCP链接自动重连
 */
public class PeriodWatcher implements Watcher {

    private static final Logger LOGGER = Logger.getLogger(PeriodWatcher.class);

    private String env;

    public PeriodWatcher(String env) {
        this.env = env;
    }

    public void process(WatchedEvent event) {

        EventType eventType = event.getType();
        String path = event.getPath();

        try {

            if (eventType == EventType.None) {
                return;
            }

            String key = PeriodTool.convertPath2Key(path);
            ZooKeeper zk = PeriodConnection.getZk(env);

            if (eventType == EventType.NodeDataChanged) {
                byte[] newValue = zk.getData(path, true, null);
                PeriodClientDataPool.add(key, new String(newValue), env);
            }

            if (eventType == EventType.NodeDeleted) {
                PeriodClientDataPool.remove(key);
            }

            if (eventType == EventType.NodeChildrenChanged) {
                List<String> children = zk.getChildren(path, true);

                for (String child : children) {
                    String childPath = path + "/" + child;
                    byte[] childData = zk.getData(childPath, true, null);
                    PeriodClientDataPool.add(PeriodTool.convertPath2Key(childPath), new String(childData), env);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Path '" + path + "' trigger event '" + event.getType().name() + "' fail.", e);
        }

    }

}
