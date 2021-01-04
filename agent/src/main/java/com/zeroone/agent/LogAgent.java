package com.zeroone.agent;

import com.zeroone.agent.transform.LogTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

public class LogAgent {


    private final static Logger log = LoggerFactory.getLogger(LogAgent.class);

    /**
     * jvm 参数形式启动，运行此方法
     * <p>方式加载时调用，即目标应用在启动时,指定了agent -javaagent:xxx.jar</p>
     */
    public static void premain(String agentArgs, Instrumentation inst) {

        log.info("===执行静态绑定===");
        inst.addTransformer(new LogTransformer());
    }

    /**
     * <p>lib: ${java.home}/../lib/tools.jar</p>
     * <p>动态 attach 方式启动，运行此方法</p>
     * <p>VirtualMachine.attach(pid); //来指定目标进程号</p>
     * <p>vm.loadAgent("...jar")加载 agent</p>
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {

        log.info("===执行动态绑定===");
        inst.addTransformer(new LogTransformer());
    }
}
