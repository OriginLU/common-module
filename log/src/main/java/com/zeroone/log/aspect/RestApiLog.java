package com.zeroone.log.aspect;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志记录
 */
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Generated
public class RestApiLog implements Serializable {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 响应参数
     */
    private String result;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * content-type
     */
    private String contentType;

    /**
     * user-agent
     */
    private String userAgent;

    /**
     * 执行时间(ms)
     */
    private long executeMs;

    /**
     * 请求uri
     */
    private String requestURI;


    /**
     * 请求URL
     */
    private String requestURL;

    /**
     * 请求ip,如果
     */
    private String ip;

    /**
     * 用户的真实ip
     */
    private String realIp;

    /**
     * 头部
     */
    private Map<String, String> headers;

    /**
     * 是否抛出异常
     */
    private boolean withThrows;

    /**
     * 抛出的异常
     */
    private Throwable throwable;

}
