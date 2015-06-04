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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            System.out.println("eventType:" + eventType + ",path:" + path + ",key:" + key);

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
                Map<String, String> childrenData = new HashMap<String, String>();

                for (String child : children) {
                    String childPath = path + "/" + child;
                    String childFullKey = PeriodTool.convertPath2Key(childPath);
                    byte[] childData = client.getData().watched().forPath(childPath);

                    System.out.println("childFullKey:" + childFullKey + ",childPath:" + childPath);

                    childrenData.put(childFullKey, new String(childData));
                }

                System.out.println("fatherKey:" + PeriodTool.convertPath2Key(path));

                PeriodClientDataPool.add(PeriodTool.convertPath2Key(path), childrenData, env);
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
