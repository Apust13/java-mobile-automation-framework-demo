package com.apust.java_framework.utils;

import io.appium.java_client.AppiumDriver;


public class AppManager {

    public static void restartApp(AppiumDriver driver) {
        try {
            MobileActions.terminateApp(driver);
            MobileActions.activateApp(driver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to restart the application", e);
        }
    }

    public static void ensureAppIsRunning(AppiumDriver driver) {
        try {
            if (!MobileActions.isAppRunning(driver)) {
                MobileActions.activateApp(driver);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure the application is running", e);
        }
    }

}
