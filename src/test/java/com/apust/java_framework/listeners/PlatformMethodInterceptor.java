package com.apust.java_framework.listeners;

import com.apust.java_framework.utils.ConfigManager;
import com.apust.java_framework.utils.locator.AndroidOnly;
import com.apust.java_framework.utils.locator.iOSOnly;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.List;
import java.util.stream.Collectors;

public class PlatformMethodInterceptor implements IMethodInterceptor {

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String platform = ConfigManager.get("platform", context, "android").toLowerCase();
        return methods.stream()
                .filter(m -> {
                    boolean isAndroidOnly = m.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(AndroidOnly.class);
                    boolean isIOSOnly = m.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(iOSOnly.class);

                    System.out.println("[Interceptor] Filtering method: " + m.getMethod().getMethodName());

                    return switch (platform) {
                        case "android" -> !isIOSOnly;
                        case "ios" -> !isAndroidOnly;
                        default -> true;
                    };
                })
                .collect(Collectors.toList());
    }
}
