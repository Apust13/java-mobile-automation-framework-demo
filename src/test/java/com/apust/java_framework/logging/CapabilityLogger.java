package com.apust.java_framework.logging;

import com.apust.java_framework.utils.ConfigManager;
import io.appium.java_client.AppiumDriver;

import java.util.List;

public class CapabilityLogger {

    private static final List<String> DEFAULT_KEYS = List.of(
            "platformName", "platformVersion", "deviceName", "udid",
            "appPackage", "appActivity", "bundleId", "automationName"
    );

    public static void logTestCapabilities(AppiumDriver driver, Loggable logger) {
        if (driver == null || driver.getSessionId() == null) {
            logger.warn("Driver is null or session is not active — skipping capability log");
            return;
        }

        logger.info("Capabilities:");
        logger.info("Session ID: " + driver.getSessionId());

        for (String key : DEFAULT_KEYS) {
            Object value = driver.getCapabilities().getCapability(key);
            if (value != null) {
                logger.info("  • " + key + ": " + value);
            }
        }
    }

    public static void logCustomCapabilities(Loggable logger) {
        logger.info("Custom Capabilities:");
        logIfPresent("testName", logger);
        logIfPresent("buildId", logger);
        logIfPresent("env", logger);
        logIfPresent("branch", logger);
    }

    private static void logIfPresent(String key, Loggable logger) {
        String value = ConfigManager.get(key);
        if (value != null) {
            logger.info("  • " + key + ": " + value);
        }
    }
}
