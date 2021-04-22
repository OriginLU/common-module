package com.zeroone.thread;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zero-one.lu
 * @since 2021-04-21
 */
public class SequencePrinter {


    public static void main(String[] args) throws InterruptedException {


        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        BlockingQueue<Object> objects = new ArrayBlockingQueue<>(26);

        for (char aChar : chars) {
            objects.add(aChar);
        }
        ExecutorService pool = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        ReentrantLock lock = new ReentrantLock();
        Runnable runnable = () -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (;;){

                if (objects.isEmpty()) {
                    if (!pool.isShutdown()) {
                        pool.shutdown();
                    }
                    return;
                }
                try {
                    lock.lock();
                    System.out.println("thread " + Thread.currentThread().getId()  + ":" + objects.poll());
                } finally {
                    if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                        lock.unlock();
                    }
                }

            }
        };

        for (int i = 0; i < 3; i++) {
            pool.submit(runnable);
            latch.countDown();
        }
    }
}
