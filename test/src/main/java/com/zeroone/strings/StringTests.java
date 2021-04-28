package com.zeroone.strings;

public class StringTests {


    /**
     * new String("a") + new String("b") 创建几个对象
     *
     * new StringBuilder()
     * new String("a")
     * new String("b")
     * "a"
     * "b"
     *
     * @param args
     */
    public static void main(String[] args) {

        String s = new String("a") + new String("b");
        s.intern();
        String s4 = "ab";
        String s5 = new String("ab");

        System.out.println(s == s4);
        System.out.println(s4 == s5);

    }
}
