package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CuratorMasterSelection {
    public static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static String masterPath = "/curator_master_path";

    public static void main(String[] args) throws Exception{
        client.start();
        LeaderSelector selector = new LeaderSelector(client, masterPath,
                new LeaderSelectorListener() {
                    public void takeLeadership(CuratorFramework client) throws Exception {
                        System.out.println("become master");
                        Thread.sleep(3000);
                        System.out.println("release master");
                    }

                    public void stateChanged(CuratorFramework client, ConnectionState newState) {

                    }
                });
        selector.autoRequeue();
        selector.start();
        Thread.sleep(10000);
    }

}
