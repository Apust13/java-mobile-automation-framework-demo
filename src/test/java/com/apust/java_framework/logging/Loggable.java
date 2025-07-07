package com.apust.java_framework.logging;

public interface Loggable {
    void step(String message, Object... args);
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Throwable t);
    void error(String message, Object... args);
    void debug(String message, Object... args);
}
