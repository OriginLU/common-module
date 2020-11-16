package com.zeroone.kafka.connect.runtime;

import com.zeroone.kafka.connect.utils.PropertiesLoader;
import org.apache.kafka.common.utils.Exit;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.cli.ConnectDistributed;
import org.apache.kafka.connect.runtime.Connect;
import org.apache.kafka.connect.runtime.WorkerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


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
                    Utils.propsToStringMap(PropertiesLoader.loadProps(workerPropsFile)) : Collections.emptyMap();

            CustomConnectDistributed connectDistributed = new CustomConnectDistributed();
            Connect connect = connectDistributed.startConnect(workerProps);

            // Shutdown will be triggered by Ctrl-C or via HTTP shutdown request
            connect.awaitStop();

        } catch (Throwable t) {
            log.error("Stopping due to error", t);
            Exit.exit(2);
        }
    }


}
