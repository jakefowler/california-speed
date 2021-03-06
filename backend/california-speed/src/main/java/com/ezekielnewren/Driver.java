package com.ezekielnewren;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StdErrLog;
import org.json.JSONObject;

import java.util.UUID;

public class Driver {
    private static final Logger log;

    static {
        // -Dorg.eclipse.jetty.LEVEL=OFF
        String tmp = System.getProperty("logger.level");
        if (tmp != null && "debug".equals(tmp) && Log.getLog() instanceof StdErrLog) {
            ((StdErrLog) Log.getLog()).setLevel(AbstractLogger.LEVEL_DEBUG);
        }

        log = Log.getLogger(Driver.class);
    }

    public static void main(String[] args) {
        try {
            Controller.go(args);
        } catch (Exception e) {
            log.warn(e);
        }
    }
}
