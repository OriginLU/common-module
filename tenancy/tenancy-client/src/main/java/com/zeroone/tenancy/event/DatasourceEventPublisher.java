package com.zeroone.tenancy.event;


import com.zeroone.tenancy.enums.DatasourceStatus;
import com.zeroone.tenancy.model.DatasourceActionEvent;
import org.springframework.context.ApplicationEventPublisher;

public class DatasourceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DatasourceEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void publishInitEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode, DatasourceStatus.INIT.getStatus()));
    }


    public void publishCreateEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode, DatasourceStatus.CREATE.getStatus()));
    }


    public void publishRunningEvent(Object source, String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode, DatasourceStatus.RUNNING.getStatus()));
    }


    public void publishRemoveEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode, DatasourceStatus.REMOVE.getStatus()));
    }


}
