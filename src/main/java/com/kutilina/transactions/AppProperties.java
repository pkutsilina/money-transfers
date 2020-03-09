package com.kutilina.transactions;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {
    private static final Logger LOGGER = Logger.getLogger(AppProperties.class);
    private static Properties values;

    static {
        values = new Properties();
        try {
            values.load(AppProperties.class.getResourceAsStream(File.separator + "application.properties"));

        } catch (IOException e) {
            LOGGER.error("Failed to load properties");
        }
    }

    public static String getProperty(String key) {
        return values.getProperty(key);
    }
}
