package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class PathChildrenCacheUsage {
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static String path = "/zk-test";

    public static void main(String[] args) throws Exception {
        client.start();
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("child added," + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("child updated," + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("child removed," + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);
        client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/1", "content1".getBytes());
        Thread.sleep(1000);
        client.setData().forPath(path + "/1", "content2".getBytes());
        Thread.sleep(1000);
        client.delete().forPath(path + "/1");
        Thread.sleep(1000);
        client.delete().forPath(path);

        Thread.sleep(100000);
    }
}
