package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CuratorDistributedAtomicInt {
    public static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        DistributedAtomicInteger value = new DistributedAtomicInteger(client, "/curator_distatomic_path",
                new RetryNTimes(1, 1000));
        AtomicValue<Integer> rc = value.add(100);
        System.out.println(rc.succeeded());
        System.out.println(rc.preValue());
        System.out.println(rc.postValue());
    }
}
