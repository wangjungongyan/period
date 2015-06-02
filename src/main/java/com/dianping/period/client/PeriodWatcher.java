package com.dianping.period.client;

import com.dianping.period.common.PeriodConnection;
import com.dianping.period.common.PeriodTool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.List;

public class PeriodWatcher implements CuratorListener {

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

            CuratorFramework client = PeriodConnection.getClient(env);

            if (eventType == EventType.NodeDataChanged) {
                byte[] newValue = client.getData().watched().forPath(
                        path);
                PeriodClientDataPool.add(key, new String(newValue), env);

            }

            if (eventType == EventType.NodeDeleted) {
                PeriodClientDataPool.remove(key, env);
            }

            if (eventType == EventType.NodeChildrenChanged) {

                List<String> children = client.getChildren().watched().forPath(path);

                for (String child : children) {
                    String childPath = path + "/" + child;
                    byte[] childData = client.getData().watched().forPath(childPath);
                    PeriodClientDataPool.add(PeriodTool.convertPath2Key(childPath), new String(childData), env);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Path '" + path + "' trigger event '" + event.getType().name() + "' fail.", e);
        }

    }

    @Override public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
        if (curatorEvent.getType() == CuratorEventType.WATCHED) {
            WatchedEvent we = curatorEvent.getWatchedEvent();
            if (we.getPath() != null) {
                process(curatorEvent.getWatchedEvent());
            }
        }
    }
}
