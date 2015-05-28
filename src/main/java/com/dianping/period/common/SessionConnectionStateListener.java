package com.dianping.period.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

/**
 * Created by vali on 15-5-28.
 */
public class SessionConnectionStateListener implements ConnectionStateListener {

    private String zkRegPathPrefix;
    private String regContent;

    public SessionConnectionStateListener(String zkRegPathPrefix, String regContent) {
        this.zkRegPathPrefix = zkRegPathPrefix;
        this.regContent = regContent;
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        if (connectionState == ConnectionState.LOST) {
            while (true) {
                try {
                    if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                        curatorFramework.create().creatingParentsIfNeeded().withMode(
                                CreateMode.EPHEMERAL_SEQUENTIAL).forPath(zkRegPathPrefix,
                                                                         regContent.getBytes("UTF-8"));
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {

                }
            }
        }
    }
}
