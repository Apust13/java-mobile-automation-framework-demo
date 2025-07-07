package com.apust.java_framework.listeners;

import com.apust.java_framework.logging.CapabilityLogger;
import com.apust.java_framework.logging.LogBridge;
import com.apust.java_framework.logging.Loggable;
import io.appium.java_client.AppiumDriver;
import org.testng.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LogLifecycleListener implements ITestListener {

    private final Map<ITestResult, Instant> startTimes = new HashMap<>();


    @Override
    public void onTestStart(ITestResult result) {
        startTimes.put(result, Instant.now());

        Loggable logger = getLogger(result);
        logger.info("STARTED: " + getTestName(result));

        logTestCapabilities(result, logger);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logCompletion(result, "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logCompletion(result, "FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logCompletion(result, "SKIPPED");
    }

    private void logCompletion(ITestResult result, String status) {
        Instant start = startTimes.getOrDefault(result, Instant.now());
        Duration duration = Duration.between(start, Instant.now());
        String time = String.format("%.2f", duration.toMillis() / 1000.0);

        getLogger(result).step(status + ": " + getTestName(result) + " (" + time + "s)");
    }

    private Loggable getLogger(ITestResult result) {
        return new LogBridge(result.getTestClass().getRealClass(), true);
    }

    private String getTestName(ITestResult result) {
        return result.getMethod().getMethodName();
    }

    private void logTestCapabilities(ITestResult result, Loggable logger) {
        Object testInstance = result.getInstance();

        if (testInstance instanceof HasDriver) {
            AppiumDriver driver = ((HasDriver) testInstance).getDriver();
            CapabilityLogger.logTestCapabilities(driver, logger);
            CapabilityLogger.logCustomCapabilities(logger);
        } else {
            logger.warn("Test instance does not implement HasDriver — skipping capability log");
        }
    }
}
