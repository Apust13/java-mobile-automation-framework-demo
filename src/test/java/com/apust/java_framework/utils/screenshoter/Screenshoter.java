package com.apust.java_framework.utils.screenshoter;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.apust.java_framework.utils.screenshoter.ScreenshotStrategyFactory.ScreenType;
import static io.qameta.allure.Allure.step;

public class Screenshoter {

    private static final String EXPECTED_DIR = "src/test/resources/screenshots/";
    private static final String ACTUAL_DIR = "build/screenshots/actual/";
    private static final String DIFF_DIR = "build/screenshots/diff/";


    public static void compareScreen(AppiumDriver driver, String name) {
        ScreenshotConfig config = resolveScreenshotConfigFromTest();
        ScreenType type = config != null ? config.type() : ScreenType.SIMPLE;
        int tolerance = config != null ? config.tolerance() : 15;

        String platform = driver.getCapabilities().getPlatformName().toString().toLowerCase();
        ShootingStrategy strategy = ScreenshotStrategyFactory.getPlatformAwareStrategy(platform, type);

        Screenshot actual = new AShot()
                .shootingStrategy(strategy)
                .takeScreenshot(driver);

        compareWithImage(name, actual.getImage(), tolerance);
    }

    public static void compareScreenCut(AppiumDriver driver, String name) {
        ScreenshotConfig config = resolveScreenshotConfigFromTest();
        ScreenType type = config != null ? config.type() : ScreenType.SIMPLE;

        String platform = driver.getCapabilities().getPlatformName().toString().toLowerCase();
        ShootingStrategy strategy = ScreenshotStrategyFactory.getPlatformAwareStrategy(platform, type);
        int tolerance = resolveToleranceFromTest();

        Screenshot actual = new AShot()
                .shootingStrategy(strategy)
                .takeScreenshot(driver);

        compareWithImage(name, actual.getImage(), tolerance);
    }

    public static void compareElement(AppiumDriver driver, WebElement element, String name) {
        int tolerance = resolveToleranceFromTest();
        Screenshot actual = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver, element);
        compareWithImage(name, actual.getImage(), tolerance);
    }

    private static void compareWithImage(String name, BufferedImage actualImage, int tolerance) {
        saveImage(actualImage, ACTUAL_DIR + name + ".png");

        File expectedFile = new File(EXPECTED_DIR + name + ".png");
        if (Boolean.getBoolean("updateScreenshots")) {
            saveImage(actualImage, expectedFile.getPath());
            System.out.println("Updated reference screenshot: " + name);
            return;
        }

        if (!expectedFile.exists()) {
            throw new RuntimeException("Expected screenshot not found: " + expectedFile.getPath());
        }

        try {
            BufferedImage expected = ImageIO.read(expectedFile);
            ImageDiff diff = new ImageDiffer().makeDiff(expected, actualImage);

            step("Screenshot tolerance: " + tolerance + " pixels");

            if (diff.getDiffSize() > tolerance) {
                File diffFile = new File(DIFF_DIR + name + "_diff.png");
                saveImage(diff.getMarkedImage(), diffFile.getPath());
                attachToAllure("Diff: " + name, diffFile);
                throw new AssertionError("Screenshot mismatch: " + name + " (diff size: " + diff.getDiffSize() + ")");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare screenshots", e);
        }
    }

    public static void saveImage(BufferedImage image, String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + path, e);
        }
    }

    private static void attachToAllure(String name, File file) {
        try (InputStream is = new FileInputStream(file)) {
            Allure.addAttachment(name, "image/png", is, ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveReferenceScreenshot(AppiumDriver driver, ScreenType type, String pathToScreenshotsReference) {
        String platform = driver.getCapabilities().getPlatformName().toString().toLowerCase();
        Screenshot actual = new AShot()
                .shootingStrategy(ScreenshotStrategyFactory.getPlatformAwareStrategy(platform, type))
                .takeScreenshot(driver);

        saveImage(actual.getImage(), pathToScreenshotsReference);
    }

    private static int resolveToleranceFromTest() {
        ScreenshotConfig config = resolveScreenshotConfigFromTest();
        return config != null ? config.tolerance() : 15;
    }

    private static ScreenshotConfig resolveScreenshotConfigFromTest() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .distinct()
                .flatMap(className -> {
                    try {
                        Class<?> clazz = Class.forName(className);
                        return Arrays.stream(clazz.getDeclaredMethods());
                    } catch (ClassNotFoundException e) {
                        return Arrays.stream(new java.lang.reflect.Method[0]);
                    }
                })
                .filter(method -> method.isAnnotationPresent(ScreenshotConfig.class))
                .map(method -> method.getAnnotation(ScreenshotConfig.class))
                .findFirst()
                .orElse(null);
    }
}
