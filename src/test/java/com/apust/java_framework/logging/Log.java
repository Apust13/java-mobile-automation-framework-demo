package com.apust.java_framework.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Log {
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
