package kk.learn;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @author yukang.chen
 * @since 8/26/15
 */
public class ZKSimpleConstructorExample implements Watcher{
    private static CountDownLatch semaphore = new CountDownLatch(1);

    public static class IStringCallback implements AsyncCallback.StringCallback {
        public void processResult(int rc, String path, Object ctx, String name) {
            System.out.println("resuld code = [" + rc + "], path = [" + path + "], ctx = [" + ctx + "], real path name = [" + name + "]");
        }
    }

    public static void main(String[] args) throws Exception{
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 3000, new ZKSimpleConstructorExample());

        // zookeeper处于connecting状态
        System.out.println(zooKeeper);

        try {
            semaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zk session established");
        System.out.println("sessionid = " + zooKeeper.getSessionId());
        System.out.println("sessionpwd = " + zooKeeper.getSessionPasswd());


        // sync create
        String path1 = zooKeeper.create("/zk-test-ephemeral-","data".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println("create znode = " + path1);

        String path2 = zooKeeper.create("/zk-test-ephemeral-","data".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create znode = " + path2);

        // async create
        zooKeeper.create("/zk-test2-phemeral-", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "context");
        zooKeeper.create("/zk-test2-phemeral-", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new IStringCallback(), "context");
        zooKeeper.create("/zk-test2-phemeral-", "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                new IStringCallback(), "context");
        Thread.sleep(3000);
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println("watchedEvent = " + watchedEvent);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            semaphore.countDown();
        }
    }
}
