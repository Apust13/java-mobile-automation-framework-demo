package com.apust.java_framework.screens;

import com.apust.java_framework.utils.locator.Android;
import com.apust.java_framework.utils.locator.iOS;
import com.apust.java_framework.utils.locator.MobileLocator;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OnboardingScreen extends BaseScreen {

    @AndroidFindBy(xpath = "//android.widget.Button[contains(@resource-id, 'skip_button')]")
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == 'Skip'`]")
    private WebElement skipButtonOld;


    @Android(xpath = "Button[contains(@resource-id, 'skip_button')]")
    @iOS(classChain = "StaticText[`name == 'Skip'`]")
    private MobileLocator skipButton;


    @Android(xpath = "//androidx.recyclerview.widget.RecyclerView[contains(@resource-id, 'languagesList')]/*[contains(@class, 'TextView')]")
    @iOS(classChain = "Table/XCUIElementTypeCell/XCUIElementTypeStaticText")
    private MobileLocator languageList;

    @Android(xpath = "Button[@text = 'Continuee']")
    @iOS(classChain = "StaticText[`name == 'Next'`]")
    private MobileLocator onboardingNextButton;


    public OnboardingScreen(AppiumDriver driver) {
        super(driver);
    }

    public MainScreen goToMainScreen() {
//        click("Skip");
        click(skipButton.by());
        return new MainScreen(driver);
    }

    public MainScreen completeOnboarding() {
         clickAndReturn("Continue")
                .clickAndReturn("Continue")
                .clickAndReturn("Continue")
                .clickAndReturn("Get started");

        return new MainScreen(driver);
    }

    public List<String> getListOfLanguages(){
        List<WebElement> elements = getElements(languageList.by());
        return getTextsFromElements(elements);
    }

    public MainScreen goThroughOnboarding(){
        clickAndReturn(onboardingNextButton.by())
                .clickAndReturn(onboardingNextButton.by())
                .clickAndReturn(onboardingNextButton.by())
                .clickAndReturn("Get started");
        return new MainScreen(driver);
    }

    public OnboardingScreen goToLanguagesTab(){
        swipeToElement(languageList.by(), 5, Direction.LEFT);
        waitUntilElementVisible(languageList.by());
        return this;
    }


}