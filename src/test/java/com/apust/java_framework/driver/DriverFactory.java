package com.apust.java_framework.driver;

import com.apust.java_framework.utils.ConfigManager;
import com.apust.java_framework.logging.LogBridge;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class DriverFactory {

    private static final ThreadLocal<AppiumDriver> driverThread = new ThreadLocal<>();
    private static final LogBridge log = new LogBridge(DriverFactory.class, false);

    public enum Platform {
        ANDROID,
        IOS
    }

    public static AppiumDriver getDriver(Platform platform, ITestContext context) {
        if (driverThread.get() == null) {
            log.info("Creating driver for platform: " + platform);

            DesiredCapabilities caps = new DesiredCapabilities();
            String appPath = "";
            caps.setCapability("platformName", platform.name());
            caps.setCapability("deviceName", ConfigManager.get("deviceName", context, "emulator"));
            caps.setCapability("platformVersion", ConfigManager.getRequired("platformVersion", context));
            caps.setCapability("automationName", ConfigManager.getRequired("automationName", context));

            caps.setCapability("adbExecTimeout", 60000);
            caps.setCapability("newCommandTimeout", 120);

            String udid = ConfigManager.get("udid", context);
            if (udid != null && !udid.isEmpty()) {
                caps.setCapability("udid", udid);
                log.info("Using udid: " + udid);
            }

            String execution = ConfigManager.get("execution", context, "local");
            String appConfigValue = ConfigManager.getRequired("app", context);

            if ("local".equalsIgnoreCase(execution)) {
                appPath = Paths.get(System.getProperty("user.dir"), appConfigValue).toString();
            } else {
                appPath = appConfigValue;
            }

            caps.setCapability("app", appPath);
            log.info("App path resolved: " + appPath);

            if (platform == Platform.ANDROID) {

                caps.setCapability("appPackage", ConfigManager.getRequired("appPackage", context));
                caps.setCapability("appActivity", ConfigManager.getRequired("appActivity", context));

            } else if (platform == Platform.IOS) {
                caps.setCapability("useNewWDA", true);
                String wdaPortStr = ConfigManager.get("wdaLocalPort", context);
                if (wdaPortStr != null && !wdaPortStr.isEmpty()) {
                    caps.setCapability("wdaLocalPort", Integer.parseInt(wdaPortStr));
                    log.info("Using wdaLocalPort: " + wdaPortStr);
                }

                caps.setCapability("bundleId", ConfigManager.get("bundleId", context));

                // For real device
//                caps.setCapability("xcodeOrgId", ConfigManager.get("xcodeOrgId", context));
//                caps.setCapability("xcodeSigningId", ConfigManager.get("xcodeSigningId", context, "iPhone Developer"));

                caps.setCapability("noReset", ConfigManager.get("noReset", context, "false"));
                caps.setCapability("newCommandTimeout", 300);
            }


            String appiumUrl = ConfigManager.getAppiumServerUrl(context);
            log.info("Appium server URL: " + appiumUrl);
            try {
                AppiumDriver driver = platform == Platform.ANDROID
                        ? new AndroidDriver(new URL(appiumUrl), caps)
                        : new IOSDriver(new URL(appiumUrl), caps);

                driverThread.set(driver);
                log.info("Driver created: sessionId = " + driver.getSessionId());
            } catch (MalformedURLException e) {
                log.error("Failed to create driver", e);
                throw new RuntimeException("Invalid Appium server URL: " + appiumUrl, e);
            }
        }

        return driverThread.get();
    }

    public static void quitDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
        }
    }
}
