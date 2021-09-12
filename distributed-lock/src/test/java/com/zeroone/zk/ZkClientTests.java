package com.zeroone.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * zookeeper api 测试
 * @author zero-one.lu
 * @since 2021-05-02
 */
public class ZkClientTests {

    public static final Logger log = LoggerFactory.getLogger(ZkClientTests.class);

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    private static final String ZK_ROOT_PATH = "/zk_distribute_lock";

    private static final String LOCK_PATH = ZK_ROOT_PATH + "/";

    private static final long DEFAULT_WAIT_TIME_MILLISECOND = 1000;


    public static void main(String[] args) {

//        createTemporarySequenceNode();
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("exit "  + scanner.nextLine());
        createPersistentNode();
        readPersistentNode();
    }


    /**
     * 创建临时节点
     */
    public static void createPersistentNode(){

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.127.8:2181")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

        try {
            String path = ZK_ROOT_PATH + "/seg/seq";
            for (int i = 0; i < 10; i++) {

                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                        .forPath(path,"121213".getBytes());
            }

            for (int i = 0; i < 10; i++) {
                path = ZK_ROOT_PATH + "/" + new Random().nextInt(100000);
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path,"121213".getBytes());
            }
        } catch (Exception e) {
            log.error("init zk client error",e);
            throw new IllegalStateException(e);
        }
    }

    public static void readPersistentNode(){

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.127.8:2181")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

        try {

            List<String> strings = curatorFramework.getChildren().forPath(ZK_ROOT_PATH);
            strings.forEach(s -> {
                try {
                    byte[] bytes = curatorFramework.getData().forPath(ZK_ROOT_PATH + "/"+ s);
                    if (bytes == null || bytes.length == 0){
                        log.info(ZK_ROOT_PATH + "/"+ s);
                        return;
                    }
                    log.info(new String(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            log.error("init zk client error",e);
            throw new IllegalStateException(e);
        }
    }



    /**
     * 创建临时节点
     */
    public static void createTemporaryNode(){

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.91.128:2181,192.168.91.128:2182,192.168.91.128:2183")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

        try {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZK_ROOT_PATH);
        } catch (Exception e) {
            log.error("init zk client error",e);
            throw new IllegalStateException(e);
        }
    }


    /**
     * 创建临时顺序节点
     */
    public static void createTemporarySequenceNode(){

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("192.168.91.128:2181,192.168.91.128:2182,192.168.91.128:2183")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

        try {
            curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ZK_ROOT_PATH);

            curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ZK_ROOT_PATH);
        } catch (Exception e) {
            log.error("init zk client error",e);
            throw new IllegalStateException(e);
        }
    }
}
