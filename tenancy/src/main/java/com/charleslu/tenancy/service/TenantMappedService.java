package com.charleslu.tenancy.service;

import com.charleslu.common.errors.CommonException;
import com.charleslu.common.errors.ErrorCode;
import com.charleslu.tenancy.config.properties.TenantConfigProperties;
import com.charleslu.tenancy.multi.util.ThreadTenantUtil;
import com.charleslu.tenancy.service.dto.TenantMappedDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import static com.charleslu.tenancy.config.properties.TenantConfigProperties.DEFAULT_HEADERS;
import static com.charleslu.tenancy.config.properties.TenantConfigProperties.DEFAULT_REQUEST;
import static com.charleslu.tenancy.constants.TenantRemote.TenancyMethods.CREATE_TENANT_BIZ_MAPPED;
import static com.charleslu.tenancy.constants.TenantRemote.TenancyMethods.QUERY_TENANT_BIZ_MAPPED;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * 租户业务映射
 *
 * @Author tanglh
 * @Date 2018/12/28 15:03
 */
@Service
public class TenantMappedService {

    private static final ParameterizedTypeReference<TenantMappedDTO> TENANT_MAPPED = new ParameterizedTypeReference<TenantMappedDTO>() {
    };


    private final String BASE_URL;
    private final RestTemplate REST_TEMPLATE;

    public TenantMappedService(TenantConfigProperties tenantConfigProperties) {
        this.BASE_URL = tenantConfigProperties.getTenancyBaseUrl();
        this.REST_TEMPLATE = tenantConfigProperties.getTenancyRestTemplate();
    }

    /**
     * 创建租户映射关系
     *
     * @param tenantMapped
     */
    public void createTenantMapped(TenantMappedDTO tenantMapped) {
        if (StringUtils.isEmpty(tenantMapped.getTenantCode())) {
            tenantMapped.setTenantCode(ThreadTenantUtil.getTenant());
        }
        Assert.hasText(tenantMapped.getTenantCode(), "业务方编码为空");
        Assert.hasText(tenantMapped.getBusinessCode(), "业务类型码为空");
        Assert.hasText(tenantMapped.getBusinessId(), "业务号为空");
        HttpEntity<TenantMappedDTO> formEntity = new HttpEntity<>(tenantMapped, DEFAULT_HEADERS);
        ResponseEntity<TenantMappedDTO> response = REST_TEMPLATE.exchange(getRequestUri(CREATE_TENANT_BIZ_MAPPED), POST, formEntity, TENANT_MAPPED);
        if (response.getStatusCode().value() < HttpStatus.OK.value() || response.getStatusCode().value() > HttpStatus.MULTIPLE_CHOICES.value()) {
            throw new CommonException(ErrorCode.process_failed);
        }
    }

    /**
     * 获取租户编码
     *
     * @param bizId
     * @param bizCode
     * @return
     */
    public String getTenantCodeByBusiness(String bizId, String bizCode) {
        Assert.hasText(bizCode, "业务类型码为空");
        Assert.hasText(bizId, "业务号为空");
        ResponseEntity<TenantMappedDTO> response = REST_TEMPLATE.exchange(getRequestUri(QUERY_TENANT_BIZ_MAPPED, bizId, bizCode), GET, DEFAULT_REQUEST, TENANT_MAPPED);
        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new CommonException(ErrorCode.process_failed);
        }
        TenantMappedDTO result = response.getBody();
        return result.getTenantCode();
    }

    /**
     * 获取请求url
     *
     * @param method
     * @param replace
     * @return
     */
    private String getRequestUri(String method, Object... replace) {
        if (null == replace) {
            return BASE_URL + method;
        }
        return BASE_URL + String.format(method, replace);
    }
}