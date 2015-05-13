package com.dianping.period;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;

public class PeriodConfigurationMonitor implements InitializingBean {

    public void afterPropertiesSet() throws Exception {
        PeriodConnection.instanceZk();
        
        ZooKeeper zk = PeriodConnection.zk;
        
        List<String> children = zk.getChildren("/period", true);
        for (String child : children) {
            zk.getChildren("/period/" + child, true);
        }

        // while (true) {
        // synchronized (PeriodSemaphore.MUTX) {
        // zk.getChildren("/period", true);
        // PeriodSemaphore.MUTX.wait();
        // }
        // }
    }

}
