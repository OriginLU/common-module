package com.zeroone.tenancy.event;


import com.zeroone.tenancy.dto.DataSourceInfo;
import com.zeroone.tenancy.enums.DatasourceAction;
import com.zeroone.tenancy.model.DatasourceActionEvent;
import org.springframework.context.ApplicationEventPublisher;

public class DatasourceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public DatasourceEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void publishInitEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode, DatasourceAction.INIT.getAction()));
    }


    public void publishCreateEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode,DatasourceAction.CREATE.getAction()));
    }


    public void publishUsingEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode,DatasourceAction.USING.getAction()));
    }


    public void publishRemoveEvent(Object source,String tenantCode){
        eventPublisher.publishEvent(DatasourceActionEvent.build(source,tenantCode,DatasourceAction.REMOVE.getAction()));
    }


}
