package com.zeroone.memory.error;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zero-one.lu
 * @since 2021-04-26
 * -Xms50m -Xmx50m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\Projects\common-module\test\target\dump.hprof
 */
public class OutMemeoryErrorTests {


    public static void main(String[] args) throws InterruptedException {

        List<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {

            list.add(new byte[1024 * 1024]);
//            Thread.sleep(500L);
        }

    }
}
