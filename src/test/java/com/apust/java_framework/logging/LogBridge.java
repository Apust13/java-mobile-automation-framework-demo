package com.apust.java_framework.logging;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogBridge implements Loggable {

    private final Logger logger;
    private final boolean logToAllure;

    public LogBridge(Class<?> clazz, boolean logToAllure) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.logToAllure = logToAllure;
    }

    @Override
    public void step(String format, Object... args) {
        String message = String.format(format.replace("{}", "%s"), args);
        logger.info("[STEP] {}", message);
        if (logToAllure) {
            Allure.step(message);
        }
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }
    @Override
    public void error(String format, Object... args) {
        logger.error(format, args);
    }

    @Override
    public void debug(String format, Object... args) {
        logger.debug(format, args);
    }
}
