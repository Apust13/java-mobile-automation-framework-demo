package com.apust.java_framework.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;

import java.util.HashMap;
import java.util.Map;

public class MobileActions {

    public static void activateApp(AppiumDriver driver) {
        String appId = getAppIdentifier(driver);
        String platform = getPlatformName(driver);

        try {
            Map<String, Object> args = new HashMap<>();
            args.put(platform.equals("ios") ? "bundleId" : "appId", appId);

            if (platform.equals("ios")) {
                driver.executeScript("mobile: launchApp", args);
            } else if (platform.equals("android")) {
                driver.executeScript("mobile: activateApp", args);
            } else {
                throw new UnsupportedOperationException("Unsupported platform: " + platform);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate the application", e);
        }
    }


    public static void terminateApp(AppiumDriver driver) {
        try {
            String appId = getAppIdentifier(driver);
            Map<String, Object> args = new HashMap<>();
            args.put("bundleId", appId);
            driver.executeScript("mobile: terminateApp", args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to terminate the application", e);
        }
    }

    private static String getAppIdentifier(AppiumDriver driver) {
        Object platform = driver.getCapabilities().getCapability(CapabilityType.PLATFORM_NAME);
        if (platform == null) {
            throw new IllegalStateException("Platform name is not specified in capabilities");
        }

        String platformName = platform.toString().toLowerCase();
        if (platformName.contains("ios")) {
            Object bundleId = driver.getCapabilities().getCapability("bundleId");
            if (bundleId == null) {
                throw new IllegalStateException("bundleId is missing in capabilities for iOS");
            }
            return bundleId.toString();
        } else if (platformName.contains("android")) {
            Object appPackage = driver.getCapabilities().getCapability("appPackage");
            if (appPackage == null) {
                throw new IllegalStateException("appPackage is missing in capabilities for Android");
            }
            return appPackage.toString();
        } else {
            throw new UnsupportedOperationException("Unsupported platform: " + platformName);
        }
    }

    public static boolean isAppRunning(AppiumDriver driver) {
        try {
            if (driver == null || driver.getSessionId() == null)
                return false;
            driver.getPageSource();
            return true;
        } catch (WebDriverException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getPlatformName(AppiumDriver driver) {
        Object platform = driver.getCapabilities().getCapability("platformName");
        if (platform == null) {
            throw new IllegalStateException("platformName is not specified in capabilities");
        }
        return platform.toString().toLowerCase();
    }

}
