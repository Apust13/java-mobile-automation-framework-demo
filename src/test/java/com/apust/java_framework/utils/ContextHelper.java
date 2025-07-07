package com.apust.java_framework.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.SupportsContextSwitching;

import java.util.Set;

public class ContextHelper {

    public static String getCurrentContext(AppiumDriver driver) {
        if (driver instanceof SupportsContextSwitching) {
            return ((SupportsContextSwitching) driver).getContext();
        }
        return "UNKNOWN";
    }

    public static Set<String> getAllContexts(AppiumDriver driver) {
        if (driver instanceof SupportsContextSwitching) {
            return ((SupportsContextSwitching) driver).getContextHandles();
        }
        return Set.of();
    }

    public static boolean switchToWebView(AppiumDriver driver) {
        if (driver instanceof SupportsContextSwitching) {
            for (String ctx : getAllContexts(driver)) {
                if (ctx.toLowerCase().startsWith("webview")) {
                    ((SupportsContextSwitching) driver).context(ctx);
                    System.out.println("Switched to WebView: " + ctx);
                    return true;
                }
            }
            System.out.println("No WebView context found.");
        }
        return false;
    }

    public static boolean switchToNative(AppiumDriver driver) {
        if (driver instanceof SupportsContextSwitching) {
            for (String ctx : getAllContexts(driver)) {
                if (ctx.equalsIgnoreCase("NATIVE_APP")) {
                    ((SupportsContextSwitching) driver).context(ctx);
                    System.out.println("Switched to Native context.");
                    return true;
                }
            }
            System.out.println("NATIVE_APP context not found.");
        }
        return false;
    }

    public static void logAvailableContexts(AppiumDriver driver) {
        System.out.println("Available contexts:");
        for (String ctx : getAllContexts(driver)) {
            System.out.println(" - " + ctx);
        }
        System.out.println("Current context: " + getCurrentContext(driver));
    }
}
