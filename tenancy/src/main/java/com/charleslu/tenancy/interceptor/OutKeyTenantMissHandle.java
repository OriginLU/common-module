package com.charleslu.tenancy.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.charleslu.common.config.resolver.OuterKeyArgumentResolver;
import com.charleslu.common.errors.CommonException;
import com.charleslu.common.errors.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 根据outkey 解析租户信息
 *
 * @Author tanglh
 * @Date 2019/1/26 20:00
 */
@Component
public class OutKeyTenantMissHandle implements TenantMissHandleInterface {
    private final Logger log = LoggerFactory.getLogger(OutKeyTenantMissHandle.class);

    @Override
    public boolean match(HttpServletRequest request) {
        Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        boolean hasOuterKey =  uriTemplateVars != null && uriTemplateVars.containsKey(OuterKeyArgumentResolver.OUTER_KEY);

        return hasOuterKey || StringUtils.isNotBlank(request.getParameter(OuterKeyArgumentResolver.OUTER_KEY)) ||
            StringUtils.isNoneBlank(request.getHeader(OuterKeyArgumentResolver.OUTER_KEY_HEADER));
    }

    @Override
    public String genTenantCode(HttpServletRequest request) {
        try {
            JsonNode jsonObject = OuterKeyArgumentResolver.getOuterKey(request);
            return jsonObject.get("tenantCode").asText(null);
        } catch (Exception e) {
            log.error("解密outerKey失败:", e);
            throw new CommonException(ErrorCode.error, e.getMessage());
        }
    }
}
