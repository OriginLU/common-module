package com.charleslu.tenancy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName: OAuth2RestConfiguration
 * @Description: RestTemplate装配Bean
 * @author tyjuncai
 * @date 2018/12/18 11:24
 */
@Configuration
public class TenantRestConfiguration {

	private static final Logger log = LoggerFactory.getLogger(TenantRestConfiguration.class);

	/**
	 * 装配负载均衡RestTemplate
	 *
	 * @return
	 */
	@Bean
	@LoadBalanced
	@Qualifier("tenantLoadBalancedRestTemplate")
	public RestTemplate tenantLoadBalancedRestTemplate() {
		log.debug("tenantLoadBalancedRestTemplate");
		return new RestTemplate(simpleClientHttpRequestFactory());
	}

	/**
	 * 装配普通RestTemplate
	 *
	 * @return
	 */
	@Bean
	@Qualifier("tenantVanillaRestTemplate")
	public RestTemplate tenantVanillaRestTemplate() {
		log.debug("tenantVanillaRestTemplate");
		return new RestTemplate(simpleClientHttpRequestFactory());
	}

	private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(){
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setReadTimeout(30000);
		return factory;
	}
}
