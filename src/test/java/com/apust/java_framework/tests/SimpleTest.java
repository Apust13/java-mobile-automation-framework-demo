package com.apust.java_framework.tests;

import com.apust.java_framework.BaseTest;
import com.apust.java_framework.utils.screenshoter.ScreenshotConfig;
import com.apust.java_framework.utils.AllureUtils;
import com.apust.java_framework.utils.screenshoter.Screenshoter;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import com.apust.java_framework.utils.screenshoter.ScreenshotStrategyFactory.ScreenType;

public class SimpleTest extends BaseTest {

    @Test(description = "Verify app launch and screenshot capture")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Simply captures a screenshot of the main screen")
    public void testScreenshot() {
        System.out.println("Screenshot is being captured");
        Allure.addAttachment("Sample Attachment", "Attachment text");
        AllureUtils.attachScreenshot(driver, "Start Screen");
    }

    @Test(description = "Main screen check")
    public void verifyMainScreenFail() {
        Screenshoter.compareScreen(driver, "main_screen");
    }


    @Test
    @ScreenshotConfig(type = ScreenType.SIMPLE, tolerance = 10)
    public void verifyMainScreen() {
        Screenshoter.compareScreenCut(driver, "main_screen_cut2");
    }


    @Test
    @ScreenshotConfig(type = ScreenType.SIMPLE, tolerance = 40)
    public void verifyMainScreenCut() {
        Screenshoter.compareScreenCut(driver, "main_screen_cut2");
    }

//  @Test()
//    public void saveReferenceScreenshot() {
//        Screenshoter.saveReferenceScreenshot(driver,
//                ScreenType.SIMPLE,
//                "src/test/resources/screenshots/main_screen_cut2.png");
//    }

}
