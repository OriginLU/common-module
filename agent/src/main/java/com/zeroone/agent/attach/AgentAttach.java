package com.zeroone.agent.attach;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AgentAttach {


//    private final static Logger log = LoggerFactory.getLogger(AgentAttach.class);

//    public static void main(String[] args) {
//
//        try {
//            List<VirtualMachineDescriptor> list = VirtualMachine.list();
//
////            for (VirtualMachineDescriptor virtualMachineDescriptor : list) {
////
////                String id = virtualMachineDescriptor.id();
////                String name = virtualMachineDescriptor.displayName();
////                log.info("agent target name:{},id:{}",name,id);
////            }
//            VirtualMachine virtualMachine = VirtualMachine.attach("2792");
//            virtualMachine.loadAgent("C:/Users/GDN9010099/IdeaProjects/common-module/agent/target/agent.jar");
//        } catch (Exception e) {
//            log.info("agent error",e);
//        }
//    }

    public static void main(String[] args) {

        System.out.println("test agent......");
    }
}
