package com.github.neboskreb.suppress.logs;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NopEffectTest {

    private final BeanWithLogger beanWithLogger = new BeanWithLogger();

    private static ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();


    @BeforeAll
    public static void beforeAll() {
        System.setOut(new PrintStream(outputCaptor));
        System.setErr(new PrintStream(outputCaptor));

        // Creating first instance triggers the initialization of Slf4j, thus printing the message we're looking for
        BeanWithLogger temp = new BeanWithLogger();

        String actual = outputCaptor.toString();
        assertThat(actual).contains("No SLF4J providers were found", "Defaulting to no-operation (NOP) logger implementation");
    }

    @AfterAll
    static void afterAll() {
        outputCaptor = null;
    }

    @Test
    void non_suppressed() {
        // WHEN
        assertThrows(Exception.class, beanWithLogger::doSomething);

        // THEN
        String actual = outputCaptor.toString();
        assertThat(actual).doesNotContain("doSomething", "Boom!", "You're dead!");
    }

    @Test
    @SuppressLogs(fieldName = "beanWithLogger")
    void suppressed() {
        // GIVEN
        outputCaptor.reset();

        // WHEN
        assertThrows(Exception.class, beanWithLogger::doSomething);

        // THEN
        String actual = outputCaptor.toString();
        assertThat(actual).doesNotContain("doSomething", "Boom!", "You're dead!");
    }
}
