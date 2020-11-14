package com.zeroone.kafka.connect.runtime;

import com.zeroone.kafka.connect.utils.ResourceLoader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.utils.Exit;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.cli.ConnectDistributed;
import org.apache.kafka.connect.runtime.Connect;
import org.apache.kafka.connect.runtime.WorkerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class CustomConnectDistributed extends ConnectDistributed {

    private static final Logger log = LoggerFactory.getLogger(ConnectDistributed.class);

    public static void main(String[] args) {
        if (args.length < 1 || Arrays.asList(args).contains("--help")) {
            log.info("Usage: ConnectDistributed worker.properties");
            Exit.exit(1);
        }

        try {
            WorkerInfo initInfo = new WorkerInfo();
            initInfo.logAll();

            String workerPropsFile = args[0];
            Map<String, String> workerProps = !workerPropsFile.isEmpty() ?
                    Utils.propsToStringMap(loadProps(workerPropsFile)) : Collections.emptyMap();

            CustomConnectDistributed connectDistributed = new CustomConnectDistributed();
            Connect connect = connectDistributed.startConnect(workerProps);

            // Shutdown will be triggered by Ctrl-C or via HTTP shutdown request
            connect.awaitStop();

        } catch (Throwable t) {
            log.error("Stopping due to error", t);
            Exit.exit(2);
        }
    }

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
