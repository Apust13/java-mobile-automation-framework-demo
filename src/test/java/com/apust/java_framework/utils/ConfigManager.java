package com.apust.java_framework.utils;

import org.testng.ITestContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {


    private static final Properties props = new Properties();

    static {
        String env = System.getProperty("env", "default").toLowerCase();
        String fileName = "config/" + env + ".properties";

        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                props.load(input);
                System.out.println("Loaded config: " + fileName);
            } else {
                System.err.println("Config file not found: " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }

    /**
     * Получает значение по ключу из system properties → testng.xml → config file
     */
    public static String get(String key, ITestContext context) {
        // 1. System property
        String value = System.getProperty(key);
        if (value != null) return value;

        // 2. testng.xml parameter
        if (context != null) {
            value = context.getCurrentXmlTest().getParameter(key);
            if (value != null) return value;
        }

        // 3. config file
        return props.getProperty(key);
    }

    public static String getRequired(String key, ITestContext context) {
        String value = get(key, context);
        if (value == null) {
            System.err.println("Config key not found: " + key);
            throw new IllegalArgumentException("Missing required config key: " + key);
        }
        return value;
    }

    public static String get(String key, ITestContext context, String defaultValue) {
        String value = get(key, context);
        return value != null ? value : defaultValue;
    }



    public static String get(String key) {
        return get(key, null);
    }

    public static String getAppiumServerUrl(ITestContext context) {
        String host = get("appiumHost", context);
        String port = get("appiumPort", context);
        return "http://" + host + ":" + port + "/";
    }




}
