package com.zeroone.kafka.connect.utils;

import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * @author zero-one.lu
 * @since 2020-11-15
 */
public class PropertiesLoader {


    public static Properties loadProps(String filename) throws IOException {
        Properties props = new Properties();

        if (filename != null) {
            List<URL> resources = ResourceLoader.getResources(filename);
            if (CollectionUtils.isEmpty(resources)){
                throw new IllegalArgumentException("can't find specified properties file [" + filename + "]");
            }

            URL url = resources.get(0);
            try (InputStream propStream = url.openStream()) {
                props.load(propStream);
            }
        } else {
            System.out.println("Did not load any properties since the property file is not specified");
        }
        return props;
    }
}
