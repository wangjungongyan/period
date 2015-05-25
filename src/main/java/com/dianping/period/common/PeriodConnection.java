package com.dianping.period.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.*;

public class PeriodConnection {

    private static String ZK_CLUSTER_PATH = "/data/period/zk/zkCluster.properties";

    private static Map<String, CuratorFramework> zkClients = initZksOfDifferentEnv();

    public static CuratorFramework getClient(String env) {
        return zkClients.get(env);
    }

    public static CuratorFramework getClient() {
        return zkClients.get(PeriodEnv.getCurrentEnv());
    }

    private static Map<String, CuratorFramework> initZksOfDifferentEnv() {

        Map<String, CuratorFramework> zkClients = new HashMap<String, CuratorFramework>();

        Map<String, String> zkClusters = getZkClustersConfig();

        Iterator envs = zkClusters.keySet().iterator();

        while (envs.hasNext()) {
            String env = (String) envs.next();
            String cluster = zkClusters.get(env);

            CuratorFramework client = CuratorFrameworkFactory.newClient(
                    cluster,
                    5000,
                    3000,
                    new ExponentialBackoffRetry(1000, 3)

            );

            client.start();

            zkClients.put(env, client);
        }

        return zkClients;

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
