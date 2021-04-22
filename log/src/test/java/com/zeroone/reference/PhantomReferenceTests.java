package com.zeroone.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

public class PhantomReferenceTests {


    public static void main(String[] args) throws InterruptedException {
        phantomReference02();
    }

    public static void phantomReference02() throws InterruptedException {

        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        byte[] bytes = new byte[1024 * 1024 * 10];
        PhantomReference<byte[]> phantomReference = new PhantomReference<>(bytes,referenceQueue);

        Reference<?> poll = referenceQueue.poll();
        bytes = null;
        System.out.println("before invoke gc，reference queue has value? " + (poll != null? "yes": "no"));
        System.out.println("before invoke gc，reference value? " + phantomReference.get());
        System.gc();

        Thread.sleep(1000L);
        System.out.println("after invke gc,ref object:" + phantomReference.get());

        Reference<?> reference = referenceQueue.poll();
        System.out.println("reference queue get value is equal Phantom reference " + (reference == phantomReference ? "yes" : "no"));


    }
}
