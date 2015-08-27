package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CreateNodeInBackground {
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static String path = "/zk-test";
    static CountDownLatch semaphore = new CountDownLatch(2);
    static ExecutorService threadPool = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws Exception {
        client.start();
        System.out.println("current thread = " + Thread.currentThread().getName());
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        System.out.println("client = [" + client + "], event = [" + event + "]");
                        System.out.println("thread process result = " + Thread.currentThread().getName());
                        semaphore.countDown();
                    }
                }, threadPool).forPath(path, "content".getBytes());

        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {

                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        System.out.println("client = [" + client + "], event = [" + event + "]");
                        System.out.println("thread process result = " + Thread.currentThread().getName());
                        semaphore.countDown();
                    }
                }).forPath(path, "content".getBytes());

        semaphore.await();
        threadPool.shutdown();
    }
}
