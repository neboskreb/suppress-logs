package com.github.neboskreb.suppress.logs;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SuppressLogsExtension.class)
public abstract class EffectTestBase {

    private final BeanWithLogger beanWithLogger = new BeanWithLogger();

    private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();


    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputCaptor));
        System.setErr(new PrintStream(outputCaptor));
    }

    @Test
    void non_suppressed() {
        // WHEN
        assertThrows(Exception.class, beanWithLogger::doSomething);

        // THEN
        String actual = outputCaptor.toString();
        assertThat(actual).contains("com.github.neboskreb.suppress.logs.BeanWithLogger", "doSomething", "Boom!", "You're dead!");
    }

    @Test
    @SuppressLogs(fieldName = "beanWithLogger")
    void suppressed() {
        // WHEN
        assertThrows(Exception.class, beanWithLogger::doSomething);

        // THEN
        String actual = outputCaptor.toString();
        assertThat(actual).doesNotContain("com.github.neboskreb.suppress.logs.BeanWithLogger", "doSomething", "Boom!", "You're dead!");
    }
}

