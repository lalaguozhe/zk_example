package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CuratorZKPathUsage {
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static String path = "/curator_zkpath_usage";

    public static void main(String[] args) throws Exception {
        client.start();
        ZooKeeper zooKeeper = client.getZookeeperClient().getZooKeeper();
        System.out.println(ZKPaths.fixForNamespace(path, "sub"));
        System.out.println(ZKPaths.makePath(path, "sub"));

        System.out.println(ZKPaths.getNodeFromPath(ZKPaths.makePath(path, "sub")));
        ZKPaths.PathAndNode pan = ZKPaths.getPathAndNode(ZKPaths.makePath(path, "sub"));
        System.out.println(pan.getPath());
        System.out.println(pan.getNode());

        String dir1 = path + "/child1";
        String dir2 = path + "/child2";
        ZKPaths.mkdirs(zooKeeper, dir1);
        ZKPaths.mkdirs(zooKeeper, dir2);
        System.out.println(ZKPaths.getSortedChildren(zooKeeper, path));
        ZKPaths.deleteChildren(zooKeeper, path, true);
        client.close();
    }
}
