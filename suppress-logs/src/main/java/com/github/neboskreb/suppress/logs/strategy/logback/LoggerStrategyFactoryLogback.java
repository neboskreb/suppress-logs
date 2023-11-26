package com.github.neboskreb.suppress.logs.strategy.logback;

import com.github.neboskreb.suppress.logs.strategy.BaseLoggerStrategyFactory;

public class LoggerStrategyFactoryLogback extends BaseLoggerStrategyFactory {
    public LoggerStrategyFactoryLogback() {
        super("ch.qos.logback.classic.Logger", LoggerStrategyLogback::new);
    }
}
