package com.apust.java_framework.utils.locator;

import java.lang.reflect.Field;


public class MobileLocatorFactory {

    public static void init(Object page, String platform) {
        for (Field field : page.getClass().getDeclaredFields()) {

            if (!isFieldSupported(field, platform)) continue;

            Android android = field.getAnnotation(Android.class);
            iOS ios = field.getAnnotation(iOS.class);

            if (android == null && ios == null) continue;

            String locator = null;
            MobileLocator.LocatorType type = null;

            if (platform.equalsIgnoreCase("Android") && android != null) {
                if (!android.xpath().isEmpty()) {
                    locator = android.xpath();
                    type = android.xpath().startsWith("//")
                            ? MobileLocator.LocatorType.XPATH_FULL
                            : MobileLocator.LocatorType.XPATH;
                } else if (!android.id().isEmpty()) {
                    locator = android.id();
                    type = MobileLocator.LocatorType.ID;
                }
            }

            if (platform.equalsIgnoreCase("iOS") && ios != null) {
                if (!ios.classChain().isEmpty()) {
                    locator = ios.classChain();
                    type = ios.classChain().startsWith("**/XCUIElementType")
                            ? MobileLocator.LocatorType.IOS_CLASS_CHAIN_FULL
                            : MobileLocator.LocatorType.IOS_CLASS_CHAIN;
                } else if (!ios.accessibilityId().isEmpty()) {
                    locator = ios.accessibilityId();
                    type = MobileLocator.LocatorType.ACCESSIBILITY_ID;
                }
            }

            if (locator != null && type != null) {
                field.setAccessible(true);
                try {
                    field.set(page, new MobileLocator(platform, locator, type));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject MobileLocator into field: " + field.getName(), e);
                }
            }
        }
    }
    private static boolean isFieldSupported(Field field, String platform) {
        if ("android".equalsIgnoreCase(platform)) {
            return !field.isAnnotationPresent(iOSOnly.class);
        } else if ("ios".equalsIgnoreCase(platform)) {
            return !field.isAnnotationPresent(AndroidOnly.class);
        }
        return true;
    }


}
