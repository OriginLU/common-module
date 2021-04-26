package com.zeroone.memory.leak;

/**
 * @author zero-one.lu
 * @since 2021-04-26
 * -Xms50m -Xmx50m
 */
public class MemoryLeakTests {



    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 20000; i++) {
            ThreadLocal<Object> objectThreadLocal = new ThreadLocal<>();
            objectThreadLocal.set(new byte[1024 * 1024]);
            Thread.sleep(500L);
        }
    }
}
