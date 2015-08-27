package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author yukang.chenO
 * @since 8/27/15
 */
public class CuratorBarrier {

    public static String barrierPath = "/curator_barrier_path";

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        CuratorFramework client = CuratorFrameworkFactory.builder()
                                .connectString("127.0.0.1:2181")
                                .sessionTimeoutMs(3000)
                                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                                .build();
                        client.start();
                        //双栅栏允许客户端在计算的开始和结束时同步。当足够的进程(threshold)加入到双栅栏时，进程开始计算， 当计算完成时，离开栅栏
                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, barrierPath, 10);
                        System.out.println(Thread.currentThread().getName());
                        barrier.enter();
                        System.out.println("enter barrier");
                        Thread.sleep(Math.round(Math.random() * 3000));
                        barrier.leave();
                        System.out.println("leave barrier");
                    } catch (Exception e) {

                    }
                }
            }).start();
        }

    }
}
