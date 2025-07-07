package com.apust.java_framework.utils.screenshoter;

import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

public class ScreenshotStrategyFactory {

    public enum ScreenType {
        SIMPLE,
        SCROLLABLE,
        MODAL
    }

    public static ShootingStrategy getPlatformAwareStrategy(String platformName, ScreenType type) {
        switch (platformName.toLowerCase()) {
            case "android":
                return getAndroidStrategy(type);
            case "ios":
                return getIosStrategy(type);
            default:
                return getDefaultStrategy(type);
        }
    }

    static ShootingStrategy getAndroidStrategy(ScreenType type) {
        switch (type) {
            case SIMPLE:
                return ShootingStrategies.cutting(
                        ShootingStrategies.simple(),
                        new FixedCutStrategy(90, 0) // обрезаем статусбар
                );
            case SCROLLABLE:
                return ShootingStrategies.viewportPasting(150);
            case MODAL:
                return ShootingStrategies.cutting(
                        ShootingStrategies.simple(),
                        new FixedCutStrategy(60, 60) // обрезаем сверху и снизу
                );
            default:
                return ShootingStrategies.simple();
        }
    }

    private static ShootingStrategy getIosStrategy(ScreenType type) {
        switch (type) {
            case SIMPLE:
            case MODAL:
                return ShootingStrategies.simple(); // iOS не требует обрезки
            case SCROLLABLE:
                return ShootingStrategies.viewportPasting(200);
            default:
                return ShootingStrategies.simple();
        }
    }

    private static ShootingStrategy getDefaultStrategy(ScreenType type) {
        switch (type) {
            case SCROLLABLE:
                return ShootingStrategies.viewportPasting(150);
            default:
                return ShootingStrategies.simple();
        }
    }
}
