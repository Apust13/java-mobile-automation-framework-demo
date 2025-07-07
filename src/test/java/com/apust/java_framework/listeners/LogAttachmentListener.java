package com.apust.java_framework.listeners;

import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class LogAttachmentListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        attachLogFile();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        attachLogFile();
    }

    private void attachLogFile() {
        File logFile = new File("build/logs/test.log");
        if (logFile.exists()) {
            try (FileInputStream fis = new FileInputStream(logFile)) {
                Allure.addAttachment("Test Log", "text/plain", fis, ".log");
            } catch (IOException e) {
                System.err.println("Failed to attach log file: " + e.getMessage());
            }
        }
    }
}
