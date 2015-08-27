package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CuratorBarrier2 {
    private static final String PATH = "/examples/barrier";
    private static final int QTY = 5;

    public static void main(String[] args) throws Exception {
        TestingServer testingServer = new TestingServer(2181);
        ExecutorService tp = Executors.newFixedThreadPool(QTY);
        CuratorFramework client = CuratorFrameworkFactory.newClient(testingServer.getConnectString(),
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        DistributedBarrier barrier = new DistributedBarrier(client, PATH);
        barrier.setBarrier();
        for (int i = 0; i < QTY; i++) {
            final DistributedBarrier dBarrier = new DistributedBarrier(client, PATH);
            final int index = i;
            Callable<Void> task = new Callable<Void>() {
                public Void call() throws Exception {
                    Thread.sleep((long) (3 * Math.random()));
                    System.out.println("Client #" + index + " waits on Barrier");
                    dBarrier.waitOnBarrier();
                    System.out.println("Client #" + index + " begins");
                    return null;
                }
            };
            tp.submit(task);
        }

        Thread.sleep(1000);
        //主线程remove barrier，等待的线程会继续执行下去
        barrier.removeBarrier();
        tp.shutdown();
        tp.awaitTermination(1, TimeUnit.MINUTES);
        CloseableUtils.closeQuietly(client);
    }
}
