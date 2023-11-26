package com.github.neboskreb.suppress.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BeanWithLogger {
    private static final Logger logger = LoggerFactory.getLogger(BeanWithLogger.class);

    public void doSomething() throws Exception {
        Exception exception = new Exception("You're dead!");
        logger.error("Boom!", exception);
        throw exception;
    }
}
