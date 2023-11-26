package com.github.neboskreb.suppress.logs.strategy;

import com.github.neboskreb.suppress.logs.LoggerStrategyFactory;
import com.github.neboskreb.suppress.logs.WrappedLogger;

import org.junit.platform.commons.support.ReflectionSupport;

import java.util.function.Function;

public class BaseLoggerStrategyFactory implements LoggerStrategyFactory {
    private final Class<?> loggerClass;
    private final Function<Object, WrappedLogger> creator;


    public BaseLoggerStrategyFactory(String canonicalName, Function<Object, WrappedLogger> creator) {
        this.loggerClass = ReflectionSupport.tryToLoadClass(canonicalName).toOptional().orElse(null);
        this.creator = creator;
    }


    @Override
    public boolean isAvailable() {
        return loggerClass != null;
    }

    @Override
    public boolean canWrap(Object actualLogger) {
        return loggerClass.isAssignableFrom(actualLogger.getClass());
    }

    @Override
    public WrappedLogger wrap(Object actualLogger) {
        return creator.apply(actualLogger);
    }
}
