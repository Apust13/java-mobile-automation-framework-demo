package com.apust.java_framework;

import com.apust.java_framework.driver.DriverFactory;
import com.apust.java_framework.driver.DriverFactory.Platform;
import com.apust.java_framework.listeners.*;
import com.apust.java_framework.logging.LogBridge;
import com.apust.java_framework.utils.AppManager;
import com.apust.java_framework.utils.ConfigManager;
import io.appium.java_client.AppiumDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;


public abstract class BaseTest implements HasDriver {
    protected AppiumDriver driver;
    protected final LogBridge log = new LogBridge(this.getClass(), true);
    protected String platformName;

    @BeforeMethod
    @Parameters({"platform"})
    public void setUp(@Optional String platformFromXml, ITestContext context) {
        platformName = ConfigManager.getRequired("platform", context);
        Platform platform = Platform.valueOf(platformName.toUpperCase());

        String appiumUrl = ConfigManager.getAppiumServerUrl(context);
        log.info("Platform: " + platform + ", Appium URL: " + appiumUrl);

        driver = DriverFactory.getDriver(platform, context);
    }

    @Override
    public AppiumDriver getDriver() {
        return driver;
    }


    public void cleanUpiOS() {
        if ("ios".equalsIgnoreCase(platformName) && driver != null) {
            AppManager.restartApp(driver);
        }
    }

    @AfterMethod
    public void tearDown() {
//        cleanUpiOS();
        DriverFactory.quitDriver();
    }
}
