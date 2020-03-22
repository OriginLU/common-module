package com.charleslu.tenancy.multi.kafka;

import com.charleslu.common.entity.BusinessPartInitNotifyDTO;
import com.charleslu.common.service.TopicMessageService;
import com.charleslu.common.util.IPUtil;
import com.charleslu.common.util.JsonUtils;
import com.charleslu.tenancy.entity.DataSourceInfo;
import com.charleslu.tenancy.service.InitTenantService;
import com.charleslu.tenancy.service.TenantDataSourceService;
import com.charleslu.tenancy.service.impl.SimpleInitTenantServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @Author tanglh
 * @Date 2018/12/19 18:01
 */
@Component
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class TenantDataSourceKafkaListener {

    private final Logger log = LoggerFactory.getLogger(TenantDataSourceKafkaListener.class);

    @Value("${eureka.instance.appname}")
    private String APP_NAME;
    @Value("${eureka.instance.instanceId}")
    private String instanceId;
    @Value("${spring.kafka.topics.tenant-data-source-info-callback:}")
    private String tenantDataSourceInfoCallbackTopic;
    @Value("${spring.kafka.topics.tenant-data-source-info}")
    private String tenantDataSourceInfo;

    @Autowired(required = false)
    private List<InitTenantService> initTenantServices;
    @Autowired
    private TenantDataSourceService tenantDataSourceService;

    @Autowired(required = false)
    private TopicMessageService topicMessageService;

    @Autowired
    private SimpleInitTenantServiceImpl simpleInitTenantService;

    /**
     * 监听通道信息 id生成 兼容低版本 group与高版本groupId
     *
     * @param record
     */
    @KafkaListener(
        id = "tenantDataSourceKafkaListener-${spring.kafka.consumer.group-id}",
        topics = "${spring.kafka.topics.tenant-data-source-info}")
    public void tenantDataSourceKafkaListener(ConsumerRecord<?, String> record) {
        log.info("tenantDataSourceKafkaListener:{}.", record);
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
        kafkaMessage.ifPresent(message -> initTenantInfo(JsonUtils.stringToObject(message, DataSourceInfo.class)));
    }

    /**
     * 初始化
     *
     * @param dataSourceInfo
     */
    public void initTenantInfo(DataSourceInfo dataSourceInfo) {
        if (null == dataSourceInfo) {
            log.debug("notify dataSourceInfo is empty.");
            return;
        }
        if (!APP_NAME.equalsIgnoreCase(dataSourceInfo.getServerName())) {
            log.debug("is not this server:{} notify.", APP_NAME);
            return;
        }
        if (CollectionUtils.isEmpty(initTenantServices)) {
            log.warn("initTenantServices is empty, cannot init.");
            return;
        }
        if (tenantDataSourceService.checkDataSource(dataSourceInfo.getTenantCode(), dataSourceInfo.getType())) {
            log.info("tenant datasource is exist");
            return;
        }
        simpleInitTenantService.initTenantInfo(dataSourceInfo);
        BusinessPartInitNotifyDTO callbackNotify = new BusinessPartInitNotifyDTO();
        callbackNotify.setAppName(APP_NAME);
        callbackNotify.setInstanceId(instanceId);
        callbackNotify.setTenantCode(dataSourceInfo.getTenantCode());
        callbackNotify.setIp(IPUtil.getServerIp());
        callbackNotify.setType(dataSourceInfo.getType());
        StringBuilder detail = new StringBuilder();
        try {
            tenantDataSourceService.setDatabaseCharsetToConfig(dataSourceInfo.getDatabase());
            initTenantServices.forEach(initTenantService ->{
                if (!simpleInitTenantService.equals(initTenantService)) {
                    callbackNotify.setSuccess(initTenantService.initTenantInfo(dataSourceInfo));
                }
            });
        } catch (Exception e) {
            log.warn("init error:{}", e);
            detail.append(e);
            callbackNotify.setSuccess(false);
        } finally {
            tenantDataSourceService.setDatabaseCharsetToUtf8mb4(dataSourceInfo.getDatabase());
        }
        callbackNotify.setDetail(detail.toString());
        if (null != topicMessageService && StringUtils.isNotBlank(tenantDataSourceInfoCallbackTopic)) {
            topicMessageService.sendAsync(tenantDataSourceInfoCallbackTopic, callbackNotify);
        }
    }
}
