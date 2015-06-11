package com.period.common;

import com.period.client.PeriodWatcher;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import java.util.*;

public class PeriodConnection {

    private static String ZK_CLUSTER_PATH = "/data/period/zk/zkCluster.properties";

    private static Map<String, CuratorFramework> zkClients = null;

    public static CuratorFramework getClient(String env) {
        initZksOfDifferentEnv();
        PeriodEnv.isSupportedEnv(env);
        return zkClients.get(env);
    }

    public static CuratorFramework getClient() {
        return getClient(PeriodEnv.getCurrentEnv());
    }

    private static void initZksOfDifferentEnv() {
        if (zkClients == null) {

            synchronized (PeriodConnection.class) {

                if (zkClients == null) {
                    Map<String, CuratorFramework> envClients = new HashMap<String, CuratorFramework>();

                    Map<String, String> zkClusters = getZkClustersConfig();

                    Iterator envs = zkClusters.keySet().iterator();

                    while (envs.hasNext()) {
                        String env = (String) envs.next();
                        String cluster = zkClusters.get(env);

                        CuratorFramework client = CuratorFrameworkFactory.newClient(
                                cluster,
                                500,
                                30000,
                                new RetryNTimes(Integer.MAX_VALUE, 1000)
                        );

                        client.getCuratorListenable().addListener(new PeriodWatcher(env));
                        client.getConnectionStateListenable().addListener(
                                new SessionConnectionStateListener("/period", ""));
                        client.start();

                        try {
                            client.getZookeeperClient().blockUntilConnectedOrTimedOut();
                        } catch (InterruptedException e) {
                            throw new RuntimeException("Init env '" + env + "' zkclient fail." + e.getMessage());
                        }

                        envClients.put(env, client);
                    }

                    zkClients = envClients;

                }
            }
        }
    }

    private static Map<String, String> getZkClustersConfig() {

        Properties prop = PeriodTool.getProperties(ZK_CLUSTER_PATH);

        Set keys = prop.keySet();

        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("Period zk cluster must set in path " + ZK_CLUSTER_PATH);
        }

        PeriodEnv.addSupportedEnv(keys);

        Map<String, String> envClusters = new HashMap<String, String>();

        Iterator it = keys.iterator();

        while (it.hasNext()) {

            String env = (String) it.next();
            String envCluster = (String) prop.get(env);

            if (envCluster == null) {
                throw new IllegalArgumentException();
            }

            envClusters.put(env, envCluster);
        }

        return envClusters;
    }

}
