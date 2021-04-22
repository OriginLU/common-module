package com.zeroone.reference;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoftReferenceTests {

    public static void main(String[] args) throws InterruptedException {

        softReference01();
    }


    public static void softReference01() throws InterruptedException {

        List<SoftReference<byte[]>> references = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {

            byte[] bytes = new byte[1024 * 1024 * 10];
            SoftReference<byte[]> weakReference = new SoftReference<>(bytes);
            references.add(weakReference);
        }
        System.gc();
        Thread.sleep(1000L);

        for (SoftReference<byte[]> reference : references) {
            System.out.println("gc 回收后：" + reference.get());
        }

    }
}
