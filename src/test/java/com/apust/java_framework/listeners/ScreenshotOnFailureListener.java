package com.apust.java_framework.listeners;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class ScreenshotOnFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();

        if (testInstance instanceof HasDriver) {
            AppiumDriver driver = ((HasDriver) testInstance).getDriver();

            if (driver != null && driver.getSessionId() != null) {
                captureFailureScreenshot(driver);
            } else {
                logStep("Driver is null or session is invalid — skipping screenshot");
            }
        } else {
            logStep("Test class does not implement HasDriver — cannot access AppiumDriver");
        }
    }

    @Step("Capturing screenshot on test failure")
    private void captureFailureScreenshot(AppiumDriver driver) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
            logStep("Screenshot successfully attached to Allure report");
        } catch (Exception e) {
            logStep("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Step("{message}")
    private void logStep(String message) {

    }
}
