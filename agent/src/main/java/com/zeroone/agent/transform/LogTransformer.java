package com.zeroone.agent.transform;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LogTransformer implements ClassFileTransformer {


    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        log.info("transform class name : {}",className);

        if (!className.equals("com/zeroone/agent/attach/AgentAttach")){
            return classfileBuffer;
        }
        ClassPool classPool = ClassPool.getDefault();
        try {
            CtClass ctClass = classPool.get(className.replaceAll("/", "."));

            CtMethod[] methods = ctClass.getDeclaredMethods();

            if (methods.length == 0){
                return classfileBuffer;
            }

            //写入日志字段域
            CtField ctField = CtField.make("private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(com.zeroone.agent.transform.LogTransformer.class);", ctClass);
            ctClass.addField(ctField);
            //设置方法日志
            for (CtMethod ctMethod : methods) {

                String name = ctMethod.getName();
                //打印参数类型
                CtClass[] parameterTypes = ctMethod.getParameterTypes();
                String log;
                if (parameterTypes.length  != 0){
                    String parameterTypeNames = Arrays.stream(parameterTypes).map(CtClass::getName).collect(Collectors.joining(","));
                    //打印参数
                    MethodInfo methodInfo = ctMethod.getMethodInfo();
                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                    String variableName = IntStream.range(0, parameterTypes.length).mapToObj(attr::variableName).collect(Collectors.joining(","));
                    //获取参数名列表
                    String prefix = format("invoke method :{},parameter types:{},args: ",name, parameterTypeNames);
                    String args = IntStream.range(0, parameterTypes.length).mapToObj(index -> "{}").collect(Collectors.joining(","));
                    //设置打印参数
                    log = "log.info(\"" + prefix + args + "\"" + "," + variableName + ");";
                }else {
                    log = "log.info(\"" + format("invoke method :{}",name) + "\");";
                }
                ctMethod.insertBefore(log);
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            log.error("transform class error",e);
        }
        return classfileBuffer;
    }


    private String format(String src,Object... args){

        return MessageFormatter.arrayFormat(src,args).getMessage();
    }
}
