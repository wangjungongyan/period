package com.period.client;

import com.period.common.PeriodEntity;
import com.period.common.PeriodTool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

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

            if (eventType == EventType.NodeDataChanged) {
                updateLocalCacheWhenNodeDataChanged(path);
            }

            if (eventType == EventType.NodeDeleted) {
                updateLocalCacheWhenNodeDeleted(path);
            }

            if (eventType == EventType.NodeChildrenChanged) {
                updateLocalCacheWhenNodeChildrenChanged(path);
            }

        } catch (Exception e) {
            LOGGER.error("Path '" + path + "' trigger event '" + event.getType().name() + "' fail.", e);
        }

    }

    private void updateLocalCacheWhenNodeDataChanged(String path) {

        String key = PeriodTool.convertPath2Key(path);

        PeriodEntity newEntity = PeriodTool.getData(key, env);

        LOGGER.info(
                "eventType: NodeDataChanged ,path:" + path + ",key:" + key + ",value:"
                + PeriodTool.convertEntity2Json(newEntity)
        );

        replaceFather(path, newEntity);
        PeriodClientDataPool.addOrCoverLocalCache(newEntity, env);
    }

    private void updateLocalCacheWhenNodeChildrenChanged(String path) {

        Map<String, PeriodEntity> childrenData = PeriodTool.getChildrenData(getPathExculdeRoot(path), env);

        PeriodClientDataPool.addOrCoverLocalCache(PeriodTool.FATHER + "_" + PeriodTool.convertPath2Key(path),
                                                  childrenData, env);
    }

    private void updateLocalCacheWhenNodeDeleted(String path) {

        String key = PeriodTool.convertPath2Key(path);

        LOGGER.info(
                "eventType: NodeDeleted ,path:" + path + ",key:" + key);

        PeriodClientDataPool.removeLocalCache(key, env);
    }

    @Override public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
        if (curatorEvent.getType() == CuratorEventType.WATCHED) {
            WatchedEvent we = curatorEvent.getWatchedEvent();
            if (we.getPath() != null) {
                process(curatorEvent.getWatchedEvent());
            }
        }
    }

    private void replaceFather(String path, PeriodEntity newEntity) {

        if (newEntity == null) return;

        String key = PeriodTool.convertPath2Key(path);

        String fatherKey = PeriodTool.getFatherKey(key.split("\\.")[0], env);

        Map<String, PeriodEntity> children = (Map<String, PeriodEntity>) PeriodClientDataPool.getLocalCache(fatherKey);

        LOGGER.info("replaceFather fatherKey:" + fatherKey + ",key:" + key);

        children.put(key, newEntity);
    }

    private String getPathExculdeRoot(String path) {
        int index = PeriodTool.ROOT_PATH.length() + 1;
        return path.substring(index);
    }

}
