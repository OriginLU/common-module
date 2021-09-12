package com.zeroone.xa;

public class HashCodeTests {


    public static void main(String[] args) {

        System.out.println(System.identityHashCode(new HashDemo("test","test1")));
        System.out.println(System.identityHashCode(new HashDemo("test","test1")));
        System.out.println(System.identityHashCode(new HashDemo("test","test1")));
    }



    public static class HashDemo{



        private String name;


        private String foo;

        public HashDemo(String name, String foo) {
            this.name = name;
            this.foo = foo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}
