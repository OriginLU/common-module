package com.zeroone.lock.zk.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 采用临时顺序节点避免羊群效应
 * @author zero-one.lu
 * @since 2021-05-02
 */

public class ZkReentrantLock implements Lock {


    public static final Logger log = LoggerFactory.getLogger(ZkReentrantLock.class);

    /**
     * sleep time
     */
    private static final int BASE_SLEEP_TIME = 1000;

    /**
     * client retry time
     */
    private static final int MAX_RETRIES = 3;

    /**
     * zookeeper lock root node
     */
    private static final String ZK_ROOT_PATH = "/zk_distribute_lock";

    /**
     * zookeeper lock children node
     */
    private static final String LOCK_PATH = ZK_ROOT_PATH + "/lock";

    /**
     * default wait time
     */
    private static final long DEFAULT_WAIT_TIME_SECOND = 1000;

    /**
     * offer lock thread
     */
    private volatile Thread thread;

    /**
     * locked path
     */
    private static final ThreadLocal<String> LOCKED_PATH = new ThreadLocal<>();

    /**
     * locke path
     */
    private static final ThreadLocal<String> LOCKED_SHORT_PATH = new ThreadLocal<>();

    private static final ThreadLocal<String> PRIOR_PATH = new ThreadLocal<>();

    private final CuratorFramework curatorFramework;

    /**
     * lock count for same thread
     */
    private final AtomicInteger lockCount = new AtomicInteger(0);

    public ZkReentrantLock(String connectString) {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        this.curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

    }


    /**
     * 加锁
     */
    @Override
    public void lock() {

        //判定是否是同一个线程
        synchronized (this) {
            if (lockCount.get() > 0) {
                if (!thread.equals(Thread.currentThread())) {
                    return;
                }
                lockCount.incrementAndGet();
                return;
            }
            thread = Thread.currentThread();
            lockCount.incrementAndGet();
        }

        try {

            //尝试加锁
            if (tryLock()) {
                return;
            }
            //尝试取锁
            for (;;) {
                //等待加锁
                await(DEFAULT_WAIT_TIME_SECOND,TimeUnit.SECONDS);
                //检查加锁情况
                if (isLocked(getWaiters())) {
                    return;
                }
            }

        } catch (Exception e) {
            unlock();
            log.error("lock error", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new InterruptedException("not support lock interrupt");
    }

    /**
     * 是否加锁成功
     */
    private boolean isLocked(List<String> waiters) {
        //节点按照编号，升序排列
        Collections.sort(waiters);

        // 如果是第一个，代表自己已经获得了锁
        String s = LOCKED_SHORT_PATH.get();
        if (s.equals(waiters.get(0))) {
            log.info("成功的获取分布式锁,节点为{}", s);
            return true;
        }
        return false;

    }

    /***
     * 获取所有加锁节点
     */
    private List<String> getWaiters() {

        try {
            return curatorFramework.getChildren().forPath(ZK_ROOT_PATH);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 等待加锁
     */
    private void await(long time, TimeUnit unit) {

        String priorPath = PRIOR_PATH.get();
        if (null == priorPath) {
            throw new IllegalStateException("prior path error");
        }

        CountDownLatch latch = new CountDownLatch(1);

        //节点变更监听器
        Watcher watcher = event -> {
            log.info("监听到的变化 watchedEvent = {}", event);
            Watcher.Event.EventType type = event.getType();
            if (Watcher.Event.EventType.NodeDataChanged == type) {
                log.info("[WatchedEvent]节点删除");
            }
            latch.countDown();
        };

        try {
            //配置节点变更监听
            curatorFramework.getData().usingWatcher(watcher).forPath(priorPath);
            boolean await;
            do {
                await = latch.await(time, unit);
            }while (!await);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /***
     * 尝试加锁
     */
    @Override
    public boolean tryLock() {

        String temporaryNode = createTemporaryNode();
        LOCKED_PATH.set(temporaryNode);
        log.info("临时顺序节点：{}", temporaryNode);
        List<String> waiters = getWaiters();
        log.info("所有等待节点：{}", waiters);
        //然后获取所有节点
        String shortPath = getShortPath(temporaryNode);
        LOCKED_SHORT_PATH.set(shortPath);
        log.info("临时顺序短节点：{}", shortPath);
        //取得加锁的排队编号
        if (isLocked(waiters)) {
            return true;
        }

        int index = Collections.binarySearch(waiters, shortPath);
        // 网络抖动，获取到的子节点列表里可能已经没有自己了
        if (index < 0) {
            log.error("网络抖动，获取到的子节点列表里可能已经没有自己了");
            throw new IllegalStateException("not found node " + shortPath);
        }
        PRIOR_PATH.set(ZK_ROOT_PATH + "/" + waiters.get(index - 1));
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit){

        if (tryLock()){
            return true;
        }
        await(time,unit);
        //检查加锁情况
        return isLocked(getWaiters());
    }

    /**
     * 获取短路径
     */
    private String getShortPath(String temporaryNode) {
        int index = temporaryNode.lastIndexOf(ZK_ROOT_PATH + "/");
        if (index >= 0) {
            index += ZK_ROOT_PATH.length() + 1;
            return index <= temporaryNode.length() ? temporaryNode.substring(index) : temporaryNode;
        }
        return null;
    }

    /**
     * 创建临时节点
     */
    private String createTemporaryNode() {
        try {
            return curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(LOCK_PATH);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * 解锁
     */
    @Override
    public void unlock() {

        if (!thread.equals(Thread.currentThread())) {
            return;
        }

        int newCount = lockCount.decrementAndGet();
        if (newCount < 0) {
            throw new IllegalStateException("lock count has gone");
        }

        if (newCount != 0) {
            return;
        }
        String lockPath = LOCKED_PATH.get();
        try {
            Stat stat = curatorFramework.checkExists().forPath(lockPath);
            if (stat != null) {
                curatorFramework.delete().forPath(lockPath);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }finally {
            LOCKED_SHORT_PATH.remove();
            LOCKED_PATH.remove();
            PRIOR_PATH.remove();
        }

    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("not support condition operation");
    }

    public static void main(String[] args) {


        Lock lock = new ZkReentrantLock("192.168.91.128:2181,192.168.91.128:2182,192.168.91.128:2183");

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    lock.lock();
                    Thread.sleep(10000L);
                    log.info("获取锁的线程为：{}",Thread.currentThread());
                }catch (Exception e){
                    log.error("error ",e);
                } finally{
                    lock.unlock();
                }
            }).start();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("exit "  + scanner.nextLine());


    }


}
