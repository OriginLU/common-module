package com.zeroone.logs;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {

     /** 
     * 重写方法拦截在方法前和方法后加入业务 
     * Object obj为目标对象 
     * Method method为目标方法 
     * Object[] params 为参数， 
     * MethodProxy proxy CGlib方法代理对象 
     */  
    @Override
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {
        System.out.println("===============");
        return methodProxy.invokeSuper(obj, args);
    }

}
