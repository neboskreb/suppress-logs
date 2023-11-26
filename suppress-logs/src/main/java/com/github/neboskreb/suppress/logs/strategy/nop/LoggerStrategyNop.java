package com.github.neboskreb.suppress.logs.strategy.nop;

import com.github.neboskreb.suppress.logs.WrappedLogger;

class LoggerStrategyNop implements WrappedLogger {

    public LoggerStrategyNop(Object actualLogger) {
        // Do nothing
    }

    @Override
    public Object overrideLevel(String newLevel) {
        // Do nothing
        return null;
    }

    @Override
    public void restoreLevel(Object oldLevel) {
        // Do nothing
    }
}
