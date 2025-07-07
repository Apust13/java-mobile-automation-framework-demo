package com.apust.java_framework.utils.locator;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class MobileLocator {

    public enum LocatorType {
        XPATH,
        XPATH_FULL,
        ID,
        IOS_CLASS_CHAIN,
        IOS_CLASS_CHAIN_FULL,
        ACCESSIBILITY_ID
    }

    private final String platform;
    private final String rawLocator;
    private final LocatorType type;
    private Object[] args = new Object[0];

    public By by() {
        return getBy();
    }

    public MobileLocator(String platform, String rawLocator, LocatorType type) {
        this.platform = platform;
        this.rawLocator = rawLocator;
        this.type = type;
    }

    public MobileLocator setArgs(Object... args) {
        this.args = args;
        return this;
    }

    private By getBy() {
        String formatted = String.format(rawLocator, args);
        return switch (type) {
            case XPATH -> By.xpath(normalizeAndroidXPath(formatted));
            case XPATH_FULL -> By.xpath(formatted);
            case ID -> By.id(formatted);
            case ACCESSIBILITY_ID -> AppiumBy.accessibilityId(formatted);
            case IOS_CLASS_CHAIN -> AppiumBy.iOSClassChain(normalizeIosClassChain(formatted));
            case IOS_CLASS_CHAIN_FULL -> AppiumBy.iOSClassChain(formatted);
        };
    }

    private String normalizeAndroidXPath(String raw) {
        if (raw.matches("^[A-Z][a-zA-Z0-9]+.*")) {
            return "//android.widget." + raw;
        }
        return raw;
    }

    private String normalizeIosClassChain(String raw) {
        if (raw.matches(".*\\*\\*/[A-Z][a-zA-Z0-9]+.*")) {
            return raw.replaceAll("\\*\\*/([A-Z][a-zA-Z0-9]+)", "**/XCUIElementType$1");
        }
        if (raw.matches("^[A-Z][a-zA-Z0-9]+.*")) {
            return "**/XCUIElementType" + raw;
        }
        return raw;
    }


    public String getRawLocator() {
        return rawLocator;
    }

    public LocatorType getType() {
        return type;
    }

    public String getPlatform() {
        return platform;
    }
}
