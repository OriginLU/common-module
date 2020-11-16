package com.zeroone.tenancy.model;

import com.zeroone.tenancy.enums.InstanceStatus;

import java.util.Map;

public class TenancyInstanceInfo {


    private String instanceName;

    private String instanceId;

    private String ip;

    private String port;

    private volatile Map<String,String> activeDataSourceMap;

    private volatile InstanceStatus instanceStatus;

    public TenancyInstanceInfo() {
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<String, String> getActiveDataSourceMap() {
        return activeDataSourceMap;
    }

    public void setActiveDataSourceMap(Map<String, String> activeDataSourceMap) {
        this.activeDataSourceMap = activeDataSourceMap;
    }

    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }
}
