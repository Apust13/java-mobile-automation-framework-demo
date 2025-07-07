package com.apust.java_framework.screens;

import com.apust.java_framework.driver.DriverFactory;
import com.apust.java_framework.logging.LogBridge;
import com.apust.java_framework.utils.locator.MobileLocatorFactory;
import com.apust.java_framework.utils.screenshoter.Screenshoter;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseScreen {

    public enum ClickableElementType {
        ANY, BUTTON, TEXT
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    protected final AppiumDriver driver;
    private static final LogBridge log = new LogBridge(DriverFactory.class, true);
    private final Duration defaultTimeout = Duration.ofSeconds(10);
    private final boolean isAndroid;

    protected WebElement elementWithText;


    public BaseScreen(AppiumDriver driver) {
        this.driver = driver;
        this.isAndroid = isAndroid();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
        MobileLocatorFactory.init(this, getPlatformName());
    }


    @Step("Wait until element is clickable: {locator}")
    public void waitUntilElementClickable(By locator) {
        Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(locator));

            log.info("Element is clickable: " + locator);
        } catch (TimeoutException e) {
            log.warn("Element was not clickable within 10 seconds: " + locator);
            throw e;
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }

    @Step("Wait until element is visible: {locator}")
    public void waitUntilElementVisible(By locator) {
        Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));

            log.info("Element is clickable: " + locator);
        } catch (TimeoutException e) {
            log.warn("Element was not clickable within 10 seconds: " + locator);
            throw e;
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }

    @Step("Click element by: '{locator}'")
    public void click(By locator) {
        log.info("Try to click element by: " + locator);
        waitUntilElementClickable(locator);
        driver.findElement(locator).click();
    }

    public BaseScreen clickAndReturn(By locator) {
        click(locator);
        return this;
    }

    private void click(ClickableElementType type, String text, boolean exact) {
        By locator = buildLocator(type, text, exact);
        waitUntilElementClickable(locator);
        driver.findElement(locator).click();
    }

    public BaseScreen clickAndReturn(String text) {
        click(text);
        return this;
    }

    @Step("Click element with exact text: '{text}'")
    public void click(String text) {
        click(ClickableElementType.ANY, text, true);
    }

    @Step("Click element containing text: '{text}'")
    public void clickContaining(String text) {
        click(ClickableElementType.ANY, text, false);
    }

    @Step("Click button with exact text: '{text}'")
    public void clickButton(String text) {
        click(ClickableElementType.BUTTON, text, true);
    }

    @Step("Click button containing text: '{text}'")
    public void clickButtonContaining(String text) {
        click(ClickableElementType.BUTTON, text, false);
    }

    @Step("Click text with exact match: '{text}'")
    public void clickText(String text) {
        click(ClickableElementType.TEXT, text, true);
    }

    @Step("Click text containing: '{text}'")
    public void clickTextContaining(String text) {
        click(ClickableElementType.TEXT, text, false);
    }

    private By buildLocator(ClickableElementType type, String text, boolean exact) {
        String className = switch (type) {
            case BUTTON -> isAndroid ? "android.widget.Button" : "XCUIElementTypeButton";
            case TEXT -> isAndroid ? "android.widget.TextView" : "XCUIElementTypeStaticText";
            case ANY -> "*";
        };

        String[] attributes = isAndroid
                ? new String[]{"@text", "@resource-id"}
                : new String[]{"label", "name"};

        String matchExpr = buildMatchExpression(attributes, text, exact);

        if (isAndroid) {
            return AppiumBy.xpath("//" + className + "[" + matchExpr + "]");
        } else {
            return AppiumBy.iOSClassChain("**/" + className + "[`" + matchExpr + "`]");
        }
    }

    private String buildMatchExpression(String[] attrs, String text, boolean exact) {
        return Arrays.stream(attrs)
                .map(attr -> {
                    if (isAndroid) {
                        return exact
                                ? attr + "='" + text + "'"
                                : "contains(" + attr + ", '" + text + "')";
                    } else {
                        return exact
                                ? attr + " BEGINSWITH[c] '" + text + "' AND " + attr + " ENDSWITH[c] '" + text + "'"
                                : attr + " CONTAINS[c] '" + text + "'";
                    }
                })
                .collect(Collectors.joining(" or "));
    }

    public boolean isAndroid() {
        return getPlatformName().equalsIgnoreCase("android");
    }

    public boolean isIOS() {
        return getPlatformName().equalsIgnoreCase("ios");
    }

    public String getPlatformName() {
        Object platform = driver.getCapabilities().getCapability(CapabilityType.PLATFORM_NAME);
        return platform != null ? platform.toString().toLowerCase() : "unknown";
    }

    public void compareScreen(String name) {
        Screenshoter.compareScreen(driver, name);
    }

    public WebElement waitForVisible(By locator) {
        return new WebDriverWait(driver, defaultTimeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return new WebDriverWait(driver, defaultTimeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void swipe(Direction direction) {
        Dimension size = driver.manage().window().getSize();
        Point start, end;

        switch (direction) {
            case UP -> {
                start = new Point(size.width / 2, (int) (size.height * 0.8));
                end = new Point(size.width / 2, (int) (size.height * 0.2));
            }
            case DOWN -> {
                start = new Point(size.width / 2, (int) (size.height * 0.2));
                end = new Point(size.width / 2, (int) (size.height * 0.8));
            }
            case LEFT -> {
                start = new Point((int) (size.width * 0.8), size.height / 2);
                end = new Point((int) (size.width * 0.2), size.height / 2);
            }
            case RIGHT -> {
                start = new Point((int) (size.width * 0.2), size.height / 2);
                end = new Point((int) (size.width * 0.8), size.height / 2);
            }
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        }

        log.info("Swipe {} from {} to {}", direction.name().toLowerCase(), start, end);
        performSwipe(start, end);
    }
    public void swipeUp() {
        swipe(Direction.UP);
    }

    public void swipeDown() {
        swipe(Direction.DOWN);
    }

    public void swipeLeft() {
        swipe(Direction.LEFT);
    }

    public void swipeRight() {
        swipe(Direction.RIGHT);
    }

    @Step("Swipe {direction} to element: {locator}")
    public void swipeToElement(By locator, int maxSwipes, Direction direction) {
        log.info("Swipe {} to element: {}", direction.name().toLowerCase(), locator);

        int attempts = 0;
        while (attempts < maxSwipes && driver.findElements(locator).isEmpty()) {
            swipe(direction);
            attempts++;
        }

        if (driver.findElements(locator).isEmpty()) {
            throw new NoSuchElementException("Element not found after " + maxSwipes + " swipes: " + locator);
        }
    }


    private void performSwipe(Point start, Point end) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), start.x, start.y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(300)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), end.x, end.y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }


    public boolean isElementVisible(By locator) {
        return isElementVisibleInternal(() -> driver.findElement(locator).isDisplayed());
    }

    public boolean isElementVisible(WebElement element) {
        return isElementVisibleInternal(element::isDisplayed);
    }

    private boolean isElementVisibleInternal(Supplier<Boolean> visibilityCheck) {
        Duration originalTimeout = driver.manage().timeouts().getImplicitWaitTimeout();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            return visibilityCheck.get();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(originalTimeout);
        }
    }

    public List<WebElement> getElements(By locator){
        return getElements(locator, 2);
    }

    public List<WebElement> getElements(By locator, int maxTries) {
        log.info("Attempt to find elements by locator: {}", locator);
        for (int attempt = 1; attempt <= maxTries; attempt++) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty()) {
                    return elements;
                }
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                log.debug("Attempt {} failed for locator {}: {}", attempt, locator, e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    public static List<String> getTextsFromElements(List<WebElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }

        return elements.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }
}
