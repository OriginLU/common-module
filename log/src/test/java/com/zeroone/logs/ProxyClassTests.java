package com.zeroone.logs;

import org.junit.Test;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyClassTests {



    @Test
    public void proxyGenerateTest(){
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/IdeaProjects/common-module/log/target/");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Student.class);
        enhancer.setCallback(new CglibProxy());

        Student student = (Student) enhancer.create();
        student.say();

    }

    public static void main(String[] args) throws InterruptedException {

        AtomicInteger atomicInteger = new AtomicInteger(0);
        ArrayBlockingQueue<Character> queue = new ArrayBlockingQueue<>(26);

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        CountDownLatch latch = new CountDownLatch(3);

        for (char aChar : chars) {
            queue.add(aChar);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                for (;;){
                    if (queue.isEmpty()) {
                        latch.countDown();
                        return;
                    }
                    System.out.println("thread: " + Thread.currentThread().getId() + ":" + queue.poll());
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

}
