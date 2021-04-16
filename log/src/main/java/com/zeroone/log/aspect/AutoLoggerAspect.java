package com.zeroone.log.aspect;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Generated
@Aspect
@Component
@Slf4j
@Order
public class AutoLoggerAspect {


    @Around("@within(com.zeroone.log.annotation.RestApiLog)")
    public Object processTx(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RestApiLog logEntity = new RestApiLog();
        Object result = null;
        //起始时间
        long startMs = System.currentTimeMillis();
        try {
            //请求
            Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .map(k -> (ServletRequestAttributes) k)
                    .ifPresent(k -> {
                        HttpServletRequest request = k.getRequest();
                        logEntity.setRequestMethod(request.getMethod());
                        logEntity.setContentType((StringUtils.isEmpty(request.getContentType())) ? "empty" : request.getContentType());
                        logEntity.setRequestURI(StringUtils.abbreviate(request.getRequestURI(), 255));
                        logEntity.setRequestURL(StringUtils.abbreviate(request.getRequestURL().toString(), 255));
                        logEntity.setUserAgent(Optional.ofNullable(request.getHeader("user-agent")).orElse(""));
                        logEntity.setIp(request.getRemoteAddr());
                        logEntity.setHeaders(getHeadersInfo(request));
                        logEntity.setRealIp(getIpAddress(request));
                    });

            //类名
            String className = joinPoint.getTarget().getClass().getName();
            logEntity.setClassName(className);
            //请求方法
            String method = joinPoint.getSignature().getName() + "()";
            logEntity.setMethod(method);
            //参数
            String methodParam = wrapArgs(joinPoint);
            logEntity.setParams(methodParam);
            //调用结果
            result = joinPoint.proceed(args);
            logEntity.setWithThrows(false);
        } catch (Throwable throwable) {
            logEntity.setWithThrows(true);
            logEntity.setThrowable(throwable);
            throw throwable;
        } finally {
            generateExecuteMs(logEntity, startMs);
            log.info(
                    "\r\n" +
                            "\r\n" +
                            "    Request URL : " + logEntity.getRequestURL() + "\r\n" +
                            "    Http Method : " + logEntity.getRequestMethod() + "\r\n" +
                            "    Request URI : " + logEntity.getRequestURI() + "\r\n" +
                            "    Request Params : " + logEntity.getParams() + "\r\n" +
                            "    Http Headers : " + logEntity.getHeaders() + "\r\n" +
                            "    Content-Type : " + logEntity.getContentType() + "\r\n" +
                            "    Class name : " + logEntity.getClassName() + "\r\n" +
                            "    Method Name : " + logEntity.getMethod() + "\r\n" +
                            "    Request IP : " + logEntity.getIp() + "\r\n" +
                            "    Real IP : " + logEntity.getRealIp() + "\r\n" +
                            "    User Agent : " + logEntity.getUserAgent() + "\r\n" +
                            "    Execution Time : " + logEntity.getExecuteMs() + "ms" + "\r\n" +
                            "    WithThrows : " + wrapThrowMessage(logEntity) + "\r\n" +
                            "    Result : " + wrapResult(result) + "\r\n" +
                            "\r\n"
            );
        }
        return result;
    }

    /**
     * 生成入参string
     */
    private String wrapArgs(ProceedingJoinPoint joinPoint) {
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        String message = IntStream.range(0, args.length).filter(i -> args[i] != null).mapToObj(i -> parameterNames[i] + "=" + StringUtils.abbreviate(args[i].toString(), 800)).collect(Collectors.joining(","));
        return "{" + message + "}";
    }

    private String wrapResult(Object result) {
        return result == null ? "<>" : StringUtils.abbreviate(result.toString(), 1500);
    }

    /**
     * 获取用户真实ip
     */
    private static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("");
                }
                ip = inet == null ? null : inet.getHostAddress();
            }
        }
        return ip;
    }

    /**
     * 获取异常信息
     */
    private String wrapThrowMessage(RestApiLog logEntity) {
        return (logEntity.isWithThrows() && logEntity.getThrowable() != null) ? (logEntity.getThrowable().getClass().getName() + "[" + logEntity.getThrowable().getMessage() + "]") : "false";
    }

    /**
     * 获取执行时间
     */
    private void generateExecuteMs(RestApiLog logEntity, long startMs) {
        //结束时间
        long endMs = System.currentTimeMillis();
        //执行时间
        long executeMs = endMs - startMs;
        logEntity.setExecuteMs(executeMs);
    }

    /**
     * 获取头部信息 && 脱敏
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, isSecret(key) ? "*******************" : value);
        }
        return map;
    }

    private boolean isSecret(String key) {
        return key == null || key.contains("auth") || key.contains("refresh") || key.contains("token");
    }


}
