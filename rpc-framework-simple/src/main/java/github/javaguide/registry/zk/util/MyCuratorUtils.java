package github.javaguide.registry.zk.util;

import github.javaguide.enums.RpcConfigEnum;
import github.javaguide.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/30  3:39 PM
 */
@Slf4j
public class MyCuratorUtils {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.info("The node was created successfully. The node is:[{}]", path);
        }

    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String serverPath) {
        if (SERVICE_ADDRESS_MAP.containsKey(serverPath)) {
            return SERVICE_ADDRESS_MAP.get(serverPath);
        }
        String zkPath = ZK_REGISTER_ROOT_PATH + "/" + serverPath;
        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath(zkPath);
            SERVICE_ADDRESS_MAP.put(serverPath, result);
            registerWatcher(serverPath, zkClient);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", zkPath);
        }
        return result;
    }

    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public static CuratorFramework getZkClient() {
        String zookeeperAddress;
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        if (properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null) {
            zookeeperAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue());
        } else {
            zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(zookeeperAddress).retryPolicy(retryPolicy).build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("zk connect timeout");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }
}
