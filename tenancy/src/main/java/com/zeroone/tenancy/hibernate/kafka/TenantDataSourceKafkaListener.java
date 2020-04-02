package com.zeroone.tenancy.hibernate.kafka;

import com.zeroone.tenancy.hibernate.spi.HibernateTenantDataSourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;

@Slf4j
public class TenantDataSourceKafkaListener {


    private HibernateTenantDataSourceProvider tenantDataSourceService;


    public TenantDataSourceKafkaListener(HibernateTenantDataSourceProvider hibernateTenantDataSourceProvider) {
        this.tenantDataSourceService = hibernateTenantDataSourceProvider;
    }

    /**
     * 监听通道信息 id生成 兼容低版本 group与高版本groupId
     */
    @KafkaListener(
            id = "tenantDataSourceKafkaListener-${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topics.tenant-data-source-info}")
    public void tenantDataSourceKafkaListener(ConsumerRecord<?, String> record) {
        log.info("tenantDataSourceKafkaListener:{}.", record);
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
//        kafkaMessage.ifPresent(message -> initTenantInfo(JsonUtils.stringToObject(message, DataSourceInfo.class)));
    }

//    /**
//     * 初始化
//     *
//     * @param dataSourceInfo
//     */
//    public void initTenantInfo(DataSourceInfo dataSourceInfo) {
//        if (null == dataSourceInfo) {
//            log.debug("notify dataSourceInfo is empty.");
//            return;
//        }
//        if (!APP_NAME.equalsIgnoreCase(dataSourceInfo.getServerName())) {
//            log.debug("is not this server:{} notify.", APP_NAME);
//            return;
//        }
//        if (CollectionUtils.isEmpty(initTenantServices)) {
//            log.warn("initTenantServices is empty, cannot init.");
//            return;
//        }
//        if (tenantDataSourceService.checkDataSource(dataSourceInfo.getTenantCode(), dataSourceInfo.getType())) {
//            log.info("tenant datasource is exist");
//            return;
//        }
//        simpleInitTenantService.initTenantInfo(dataSourceInfo);
//        BusinessPartInitNotifyDTO callbackNotify = new BusinessPartInitNotifyDTO();
//        callbackNotify.setAppName(APP_NAME);
//        callbackNotify.setInstanceId(instanceId);
//        callbackNotify.setTenantCode(dataSourceInfo.getTenantCode());
//        callbackNotify.setIp(IPUtil.getServerIp());
//        callbackNotify.setType(dataSourceInfo.getType());
//        StringBuilder detail = new StringBuilder();
//        try {
//            tenantDataSourceService.setDatabaseCharsetToConfig(dataSourceInfo.getDatabase());
//            initTenantServices.forEach(initTenantService ->{
//                if (!simpleInitTenantService.equals(initTenantService)) {
//                    callbackNotify.setSuccess(initTenantService.initTenantInfo(dataSourceInfo));
//                }
//            });
//        } catch (Exception e) {
//            log.warn("init error:{}", e);
//            detail.append(e);
//            callbackNotify.setSuccess(false);
//        } finally {
//            tenantDataSourceService.setDatabaseCharsetToUtf8mb4(dataSourceInfo.getDatabase());
//        }
//        callbackNotify.setDetail(detail.toString());
//        if (null != topicMessageService && StringUtils.isNotBlank(tenantDataSourceInfoCallbackTopic)) {
//            topicMessageService.sendAsync(tenantDataSourceInfoCallbackTopic, callbackNotify);
//        }
//    }
}
