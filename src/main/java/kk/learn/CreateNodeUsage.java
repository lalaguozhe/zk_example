package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CreateNodeUsage {
    public static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception{
        client.start();
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath("/zk-test/kk-", "kk".getBytes("UTF-8"));
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/zk-test");
        client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath("/zk-test");
        client.close();
    }
}
