package com.github.neboskreb.suppress.logs;

public interface LoggerStrategyFactory {
    boolean isAvailable();
    boolean canWrap(Object actualLogger);
    WrappedLogger wrap(Object actualLogger);

}
