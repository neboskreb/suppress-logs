package com.github.neboskreb.suppress.logs.strategy.jdk14;

import com.github.neboskreb.suppress.logs.strategy.BaseLoggerStrategyFactory;

public class LoggerStrategyFactoryJdk14 extends BaseLoggerStrategyFactory {
    public LoggerStrategyFactoryJdk14() {
        super("org.slf4j.jul.JDK14LoggerAdapter", LoggerStrategyJdk14::new);
    }
}
