package com.github.neboskreb.suppress.logs;

public interface WrappedLogger {
    Object overrideLevel(String newLevel);

    void restoreLevel(Object oldLevel);
}
