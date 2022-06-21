package github.javaguide.registry.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Arrays;
import java.util.List;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/21  11:28 AM
 */
public class TestZk {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) throws Exception {
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString("hadoop1:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        zkClient.create().forPath("/node1/node1.1");
        zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1/node1.2");
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/tmp1.1");
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001","java".getBytes());
        zkClient.getData().forPath("/node1/00001");//获取节点的数据内容，获取到的是 byte数组
        zkClient.checkExists().forPath("/node1/00001");
        zkClient.getData().forPath("/node1/00001");//获取节点的数据内容
        zkClient.setData().forPath("/node1/00001","c++".getBytes());//更新节点数据内容
        List<String> childrenPaths = zkClient.getChildren().forPath("/node1");
        System.out.println(Arrays.toString(childrenPaths.toArray()));
        zkClient.close();
    }
}
