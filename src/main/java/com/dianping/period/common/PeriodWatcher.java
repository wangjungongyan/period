package com.dianping.period.common;

import com.dianping.period.client.PeriodClientDataPool;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * 1、TCP链接自动重连
 * 2、支持集群
 */
public class PeriodWatcher implements Watcher {

    private static final Logger LOGGER = Logger.getLogger(PeriodWatcher.class);

    public void process(WatchedEvent event) {

        EventType eventType = event.getType();
        String path = event.getPath();
        String key = PeriodTool.convertPath2Key(path);

        try {
            if (eventType == EventType.NodeDataChanged) {
                byte[] newValue = PeriodConnection.getZk().getData(path, true, null);
                PeriodClientDataPool.add(key, new String(newValue));
            }

            if (eventType == EventType.NodeDeleted) {
                PeriodClientDataPool.remove(key);
            }

        } catch (Exception e) {
            LOGGER.error("Path '" + path + "' trigger event '" + event.getType().name() + "' fail.", e);
        }

    }

}
