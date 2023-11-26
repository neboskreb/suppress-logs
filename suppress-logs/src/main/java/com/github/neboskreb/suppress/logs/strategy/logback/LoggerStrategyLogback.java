package com.github.neboskreb.suppress.logs.strategy.logback;

import com.github.neboskreb.suppress.logs.WrappedLogger;

class LoggerStrategyLogback implements WrappedLogger {
    private final ch.qos.logback.classic.Logger logger;

    public LoggerStrategyLogback(Object actualLogger) {
        logger = (ch.qos.logback.classic.Logger) actualLogger;
    }

    @Override
    public Object overrideLevel(String newLevel) {
        ch.qos.logback.classic.Level oldLevel = logger.getLevel();
        ch.qos.logback.classic.Level level = asLevel(newLevel);
        logger.setLevel(level);

        return oldLevel;
    }

    @Override
    public void restoreLevel(Object oldLevel) {
        ch.qos.logback.classic.Level level = (ch.qos.logback.classic.Level) oldLevel;
        logger.setLevel(level);
    }

    private ch.qos.logback.classic.Level asLevel(String value) {
        switch (value) {
            case "OFF":
                return ch.qos.logback.classic.Level.OFF;
            case "ERROR":
                return ch.qos.logback.classic.Level.ERROR;
            case "WARN":
                return ch.qos.logback.classic.Level.WARN;
            case "INFO":
                return ch.qos.logback.classic.Level.INFO;
            case "DEBUG":
                return ch.qos.logback.classic.Level.DEBUG;
            case "TRACE":
                return ch.qos.logback.classic.Level.TRACE;
            case "ALL":
                return ch.qos.logback.classic.Level.ALL;
            default:
                throw new IllegalArgumentException("Invalid log level: " + value);
        }
    }
}
