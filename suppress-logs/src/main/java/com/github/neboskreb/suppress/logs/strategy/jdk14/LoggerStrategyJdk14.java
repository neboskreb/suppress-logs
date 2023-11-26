package com.github.neboskreb.suppress.logs.strategy.jdk14;

import com.github.neboskreb.suppress.logs.WrappedLogger;

import org.apache.commons.lang3.reflect.FieldUtils;

class LoggerStrategyJdk14 implements WrappedLogger {
    private final java.util.logging.Logger logger;

    public LoggerStrategyJdk14(Object actualLogger) {
        try {
            org.slf4j.jul.JDK14LoggerAdapter adapter = (org.slf4j.jul.JDK14LoggerAdapter) actualLogger;
            logger = (java.util.logging.Logger) FieldUtils.readField(adapter, "logger", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object overrideLevel(String newLevel) {
        java.util.logging.Level oldLevel = logger.getLevel();
        java.util.logging.Level level = asLevel(newLevel);
        logger.setLevel(level);

        return oldLevel;
    }

    @Override
    public void restoreLevel(Object oldLevel) {
        java.util.logging.Level level = (java.util.logging.Level) oldLevel;
        logger.setLevel(level);
    }

    private java.util.logging.Level asLevel(String value) {
        switch (value) {
            case "OFF":
                return java.util.logging.Level.OFF;
            case "SEVERE":
                return java.util.logging.Level.SEVERE;
            case "WARNING":
                return java.util.logging.Level.WARNING;
            case "INFO":
                return java.util.logging.Level.INFO;
            case "CONFIG":
                return java.util.logging.Level.CONFIG;
            case "FINE":
                return java.util.logging.Level.FINE;
            case "FINER":
                return java.util.logging.Level.FINER;
            case "FINEST":
                return java.util.logging.Level.FINEST;
            case "ALL":
                return java.util.logging.Level.ALL;
            default:
                throw new IllegalArgumentException("Invalid log level: " + value);
        }
    }
}
