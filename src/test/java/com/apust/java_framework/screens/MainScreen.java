package com.apust.java_framework.screens;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class MainScreen extends BaseScreen{


    @AndroidFindBy(id = "org.wikipedia.alpha:id/main_toolbar_wordmark")
    @iOSXCUITFindBy(accessibility = "wikipedia")
    private WebElement logo;


    public MainScreen(AppiumDriver driver) {
        super(driver);
    }

    public boolean isMainPageOpened(){
        return isElementVisible(logo);
    }
}
