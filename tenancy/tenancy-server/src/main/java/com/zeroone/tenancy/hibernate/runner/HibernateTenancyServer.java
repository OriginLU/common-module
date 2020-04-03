package com.zeroone.tenancy.hibernate.runner;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
public class HibernateTenancyServer implements SmartInitializingSingleton, InitializingBean {




    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
