package kk.learn;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.Collection;

/**
 * @author yukang.chen
 * @since 8/27/15
 */
public class CuratorTransactionUsage {
    public static void main(String[] args) throws Exception{
        TestingServer server = new TestingServer();
        CuratorFramework client = null;
        try {
            client = CuratorFrameworkFactory.builder()
                    .connectString(server.getConnectString())
                    .sessionTimeoutMs(30000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();
            client.start();

            Collection<CuratorTransactionResult> results = client.inTransaction().create().forPath("/abc", "1".getBytes())
                    .and().setData().forPath("/abc", "2".getBytes())
                    .and().delete().forPath("/abc")
                    .and().commit();

            for (CuratorTransactionResult result : results) {
                System.out.println("result type = " + result.getType());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
            CloseableUtils.closeQuietly(server);
        }


    }
}
