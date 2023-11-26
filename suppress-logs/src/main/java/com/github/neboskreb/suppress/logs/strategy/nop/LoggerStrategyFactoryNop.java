package com.github.neboskreb.suppress.logs.strategy.nop;

import com.github.neboskreb.suppress.logs.strategy.BaseLoggerStrategyFactory;

public class LoggerStrategyFactoryNop extends BaseLoggerStrategyFactory {
    public LoggerStrategyFactoryNop() {
        super("org.slf4j.helpers.NOPLogger", LoggerStrategyNop::new);
    }
}
