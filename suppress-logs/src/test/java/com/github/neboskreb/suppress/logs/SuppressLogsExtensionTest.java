package com.github.neboskreb.suppress.logs;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SuppressLogsExtension.class)
public class SuppressLogsExtensionTest {

    private final BeanWithLogger beanWithLogger = new BeanWithLogger();

    @Test
    void testParameterInjection(@SuppressLogs BeanWithLogger beanWithLogger) {
        assertThrows(Exception.class, beanWithLogger::doSomething);
    }

    @Test
    @SuppressLogs(fieldName = "beanWithLogger")
    void testMethodInjection() {
        assertThrows(Exception.class, beanWithLogger::doSomething);
    }
}

