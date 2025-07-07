# Java Mobile Automation Framework

- Appium + TestNG
- Custom annotations (@Android, @iOS, @ScreenshotConfig)
- Visual testing with pixel tolerance
- Allure integration
- Parallel execution & platform filtering
- Gradle tasks for CI/CD

## Quick Start

### Install dependencies
./gradlew clean build

### Start Appium (background)
./gradlew 01_startAppium4723

### Start Appium (terminal with logs)
./gradlew 01_startAppiumConsole4723

## Create and / or run devices (emulators and simulators)
#### For example:
- Android 16.0, Pixel 9 
- iOS 18.5, iPhone 16 Plus

## App files
There are 2 files app-debug.apk and app-debug.7z

for iOS you need to unzip the app-debug .7z file to get app-debug.zip

## Run Tests

- `04_testDefault` — Runs `testng.xml`
- `05_testAndroid` — Android suite
- `05_testAndroidParallel` — Android in parallel
- `06_testIos` — iOS suite
- `06_testIosParallel` — iOS in parallel

##### Example:
./gradlew 05_testAndroid -Dplatform=android

## Allure Reports
##### Generate:
./gradlew 07_allureGenerate

##### Serve in browser:
./gradlew 08_openAllureServe

##### Clean:
./gradlew 10_cleanAllure




