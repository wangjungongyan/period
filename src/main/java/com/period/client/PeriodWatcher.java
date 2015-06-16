package com.period.client;

import com.google.common.base.Charsets;
import com.period.common.PeriodConnection;
import com.period.common.PeriodEntity;
import com.period.common.PeriodTool;
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
            CuratorFramework client = PeriodConnection.getClient(env);

            if (eventType == EventType.NodeDataChanged) {
                byte[] newValue = client.getData().watched().forPath(
                        path);

                LOGGER.info(
                        "eventType:" + eventType + ",path:" + path + ",key:" + key + ",value:" + new String(newValue,
                                                                                                            Charsets.UTF_8)
                );

                PeriodEntity newEntity = PeriodTool.convertJson2Entity(new String(newValue, Charsets.UTF_8));
                replaceFather(path, newEntity);
                PeriodClientDataPool.addLocalCache(newEntity, env);

            }

            if (eventType == EventType.NodeDeleted) {
                LOGGER.info(
                        "eventType:" + eventType + ",path:" + path + ",key:" + key);

                PeriodClientDataPool.removeLocalCache(key, env);
            }

            if (eventType == EventType.NodeChildrenChanged) {

                List<String> children = client.getChildren().watched().forPath(path);

                if (children == null || children.size() == 0) {
                    PeriodClientDataPool.addLocalCache(PeriodTool.FATHER + "_" + PeriodTool.convertPath2Key(path),
                                                       null, env);
                }

                Map<String, PeriodEntity> childrenData = new HashMap<String, PeriodEntity>();

                for (String child : children) {
                    String childPath = path + "/" + child;
                    String childFullKey = PeriodTool.convertPath2Key(childPath);
                    byte[] childData = client.getData().watched().forPath(childPath);

                    LOGGER.info("childFullKey:" + childFullKey + ",childPath:" + childPath + ",value:" + new String(
                            childData, Charsets.UTF_8));

                    childrenData.put(childFullKey,
                                     PeriodTool.convertJson2Entity(new String(childData, Charsets.UTF_8)));
                }

                PeriodClientDataPool.addLocalCache(PeriodTool.FATHER + "_" + PeriodTool.convertPath2Key(path),
                                                   childrenData, env);
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

    private void replaceFather(String path, PeriodEntity newEntity) {

        String key = PeriodTool.convertPath2Key(path);
        String fatherKey = getFatherKey(key);

        Map<String, PeriodEntity> children = (Map<String, PeriodEntity>) PeriodClientDataPool.getLocalCache(fatherKey);

        LOGGER.info("replaceFather fatherKey:" + fatherKey + ",key:" + key);

        children.put(key, newEntity);
    }

    private String getFatherKey(String key) {
        return env + "_" + PeriodTool.FATHER + "_" + key.split("\\.")[0];
    }

}
