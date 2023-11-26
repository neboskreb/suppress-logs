package com.github.neboskreb.suppress.logs.strategy.reload4j;

import com.github.neboskreb.suppress.logs.strategy.BaseLoggerStrategyFactory;

public class LoggerStrategyFactoryReload4j extends BaseLoggerStrategyFactory {
    public LoggerStrategyFactoryReload4j() {
        super("org.slf4j.reload4j.Reload4jLoggerAdapter", LoggerStrategyReload4j::new);
    }
}
