package com.github.neboskreb.suppress.logs.strategy.reload4j;

import com.github.neboskreb.suppress.logs.WrappedLogger;

import org.apache.commons.lang3.reflect.FieldUtils;


class LoggerStrategyReload4j implements WrappedLogger {
    private final org.apache.log4j.Logger logger;

    public LoggerStrategyReload4j(Object actualLogger) {
        try {
            org.slf4j.reload4j.Reload4jLoggerAdapter adapter = (org.slf4j.reload4j.Reload4jLoggerAdapter) actualLogger;
            logger = (org.apache.log4j.Logger) FieldUtils.readField(adapter, "logger", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object overrideLevel(String newLevel) {
        org.apache.log4j.Level oldLevel = logger.getLevel();
        org.apache.log4j.Level level = asLevel(newLevel);
        logger.setLevel(level);

        return oldLevel;
    }

    @Override
    public void restoreLevel(Object oldLevel) {
        org.apache.log4j.Level level = (org.apache.log4j.Level) oldLevel;
        logger.setLevel(level);
    }

    private org.apache.log4j.Level asLevel(String value) {
        switch (value) {
            case "OFF":
                return org.apache.log4j.Level.OFF;
            case "FATAL":
                return org.apache.log4j.Level.FATAL;
            case "ERROR":
                return org.apache.log4j.Level.ERROR;
            case "WARN":
                return org.apache.log4j.Level.WARN;
            case "INFO":
                return org.apache.log4j.Level.INFO;
            case "DEBUG":
                return org.apache.log4j.Level.DEBUG;
            case "TRACE":
                return org.apache.log4j.Level.TRACE;
            case "ALL":
                return org.apache.log4j.Level.ALL;
            default:
                throw new IllegalArgumentException("Invalid log level: " + value);
        }
    }
}
