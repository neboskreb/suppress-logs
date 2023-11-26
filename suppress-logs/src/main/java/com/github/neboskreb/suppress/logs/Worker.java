package com.github.neboskreb.suppress.logs;

class Worker {
    public WrappedLogger logger;
    public String newLevel;
    public Object oldLevel;

    public Worker(WrappedLogger logger, String newLevel) {
        this.logger = logger;
        this.newLevel = newLevel;
    }

    public void before() {
        oldLevel = logger.overrideLevel(newLevel);
    }

    public void after() {
        logger.restoreLevel(oldLevel);
    }
}
