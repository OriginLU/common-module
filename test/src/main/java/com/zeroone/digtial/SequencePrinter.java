package com.zeroone.digtial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zero-one.lu
 * @since 2021-04-25
 */
public class SequencePrinter {




    private static final Logger log = LoggerFactory.getLogger(SequencePrinter.class);

    static int count = 0;
    public static void main(String[] args) throws InterruptedException {


        char[] chars = "AliAliAliAliAliAliAliAliAliAliAliAli".toCharArray();
        ReentrantLock lock = new ReentrantLock();
        Condition conditonA = lock.newCondition();
        Condition conditonB = lock.newCondition();
        Condition conditonC = lock.newCondition();

        CountDownLatch deadLatch = new CountDownLatch(3);

        new Thread(() -> {


            while (true){
                try {
                    lock.lock();
                    if (chars.length <= count){
                        conditonB.signal();
                        deadLatch.countDown();
                        break;
                    }
                    if (chars[count] == 'A'){
                        System.out.print(chars[count ++]);
                        conditonB.signal();
                        conditonA.await();
                    }
                } catch (InterruptedException e) {
                    log.error("error",e);
                }finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }

        }).start();
        new Thread(() -> {


            while (true){
                try {
                    lock.lock();
                    if (chars.length <= count){
                        conditonC.signal();
                        deadLatch.countDown();
                        break;
                    }
                    if (chars[count] == 'l'){
                        System.out.print(chars[count ++]);
                        conditonC.signal();
                        conditonB.await();
                    }
                } catch (InterruptedException e) {
                    log.error("error",e);
                }finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        }).start();
        new Thread(() -> {

            while (true){
                try {
                    lock.lock();
                    if (chars.length <= count){
                        conditonA.signal();
                        deadLatch.countDown();
                        break;
                    }
                    if (chars[count] == 'i'){
                        System.out.println(chars[count ++]);
                        conditonA.signal();
                        conditonC.await();
                    }
                } catch (InterruptedException e) {
                    log.error("error",e);
                }finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }

        }).start();

        deadLatch.await();

    }
}
