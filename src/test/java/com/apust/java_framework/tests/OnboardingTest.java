package com.apust.java_framework.tests;

import com.apust.java_framework.BaseTest;
import com.apust.java_framework.screens.MainScreen;
import com.apust.java_framework.screens.OnboardingScreen;
import com.apust.java_framework.utils.locator.AndroidOnly;
import com.apust.java_framework.utils.locator.iOSOnly;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Onboarding")
@Feature("Welcome screen")
public class OnboardingTest extends BaseTest {

    @Test
    public void skipOnboardingTest() {
        MainScreen mainScreen = new OnboardingScreen(driver).
                goToMainScreen();
        Assert.assertTrue(mainScreen.isMainPageOpened(), "Expected 'Wikipedia' title to be visible after onboarding");
    }

    @Test
    public void continueOnboardingTest() {
        MainScreen mainScreen = new OnboardingScreen(driver).goThroughOnboarding();
        Assert.assertTrue(mainScreen.isMainPageOpened(), "Expected 'Wikipedia' title to be visible after onboarding");
    }

    @Test
    @AndroidOnly
    public void androidCheckDefaultLanguageIsSetTest() {
         int actValue = new OnboardingScreen(driver).getListOfLanguages().size();
        Assert.assertEquals(actValue, 1, "List of default languages on Android does not have items.");
    }

    @Test
    @iOSOnly
    public void iOSCheckDefaultLanguageAreSetTest() {
        int actValue = new OnboardingScreen(driver)
                .goToLanguagesTab()
                .getListOfLanguages().size();
        Assert.assertTrue(actValue > 0, "List of default languages on iOS does not have items.");
    }

}