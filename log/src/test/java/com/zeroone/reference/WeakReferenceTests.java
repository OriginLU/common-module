package com.zeroone.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeakReferenceTests {


    public static void main(String[] args) throws InterruptedException {

        weakReference02();
//        weakReference01();
    }


    public static void weakReference02() throws InterruptedException {

        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        byte[] bytes = new byte[1024 * 1024 * 10];
        WeakReference<byte[]> weakReference = new WeakReference<>(bytes,referenceQueue);

        Reference<?> poll = referenceQueue.poll();
        bytes = null;
        System.out.println("before invoke gc,reference queue has value? " + (poll != null? "yes": "no"));
        System.out.println("before invoke gc,reference value? " + weakReference.get());
        System.gc();

        Thread.sleep(1000L);
        System.out.println("after invke gc,ref object:" + weakReference.get());

        Reference<?> reference = referenceQueue.poll();
        System.out.println("reference queue get value is equal weakreference " + (reference == weakReference ? "yes" : "no"));


    }



    public static void weakReference01() throws InterruptedException {

        List<WeakReference<byte[]>> references = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {

            byte[] bytes = new byte[1024 * 1024 * 10];
            WeakReference<byte[]> weakReference = new WeakReference<>(bytes);
            references.add(weakReference);
        }
        System.gc();
        Thread.sleep(1000L);

        for (WeakReference<byte[]> reference : references) {
            System.out.println("gc 回收后：" + Arrays.toString(reference.get()));
        }

    }
}
