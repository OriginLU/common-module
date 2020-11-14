package com.zeroone.kafka.connect.utils;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;


public class ResourceLoader {


    public final static Logger log = LoggerFactory.getLogger(ResourceLoader.class);

    private ResourceLoader() {
    }


    public static List<URL> getResources(String resourceName){

        List<URL> urls = Lists.newArrayList();
        ClassLoader[] classLoaders = classLoaders();
        for (ClassLoader classLoader : classLoaders) {
            try {
                Enumeration<URL>  resources = classLoader.getResources(resourceName);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if(!contain(url,urls)){
                        urls.add(url);
                    }
                }
            } catch (Exception e) {
                log.error("load resource[{}] error,error message:{}",resourceName, Throwables.getRootCause(e).getMessage());
            }

        }
        return urls;
    }

    private static boolean contain(URL url, List<URL> urls) {

        if (urls == null || urls.size() == 0){
            return false;
        }
        return urls.stream().anyMatch(u -> u.getPath().equals(url.getPath()));
    }

    public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
        if (classLoaders != null && classLoaders.length != 0) {
            return classLoaders;
        } else {
            ClassLoader contextClassLoader = contextClassLoader(), staticClassLoader = staticClassLoader();
            return contextClassLoader != null ?
                    staticClassLoader != null && contextClassLoader != staticClassLoader ?
                            new ClassLoader[]{contextClassLoader, staticClassLoader} :
                            new ClassLoader[]{contextClassLoader} :
                    new ClassLoader[] {};

        }
    }

    public static ClassLoader contextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    public static ClassLoader staticClassLoader() {
        return ResourceLoader.class.getClassLoader();
    }
}
