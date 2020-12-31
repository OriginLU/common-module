package com.zeroone.agent;

import com.zeroone.agent.transform.LogTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * <p>|---------------|-----------------------------------------------------------|--------------------------------</p>
 * <p>|方法	          |                     说明	                                  |    使用姿势</p>
 * <p>|---------------|-----------------------------------------------------------|---------------------------------------</p>
 * <p>|premain()	  |     agent 以 jvm 方式加载时调用，即目标应用在启动时,指定了agent  |-javaagent:xxx.jar</p>
 * <p>|---------------|-----------------------------------------------------------|------------------------------------------</p>
 * <p>|agentmain()	  |      agent 以 attach 方式运行时调用,目标应用程序正常工作时使用   |VirtualMachine.attach(pid)来指定目标进程号vm.loadAgent("...jar")加载 agent</p>
 * <p>|---------------|-----------------------------------------------------------|--------------------------------------------------------</p>
 */
public class LogAgent {


    private final static Logger log = LoggerFactory.getLogger(LogAgent.class);

    /**
     * jvm 参数形式启动，运行此方法
     */
    public static void premain(String agentArgs, Instrumentation inst) {

        log.info("===执行静态绑定===");
        inst.addTransformer(new LogTransformer());
    }

    /**
     * 动态 attach 方式启动，运行此方法
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {

        log.info("===执行动态绑定===");
        inst.addTransformer(new LogTransformer());
    }
}
