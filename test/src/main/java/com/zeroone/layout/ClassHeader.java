package com.zeroone.layout;

import org.openjdk.jol.info.ClassLayout;

import java.util.HashMap;
import java.util.Map;

public class ClassHeader {

    public static void main(String[] args) {


        Map<String, Object> map = new HashMap<>();

        System.out.println(ClassLayout.parseInstance(map).toPrintable());
    }
}
