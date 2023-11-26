# Suppress Logs extension
JUnit 5 extension for temporary suppressing the error logging when error is expected, e.g. in negative tests.

# What is the problem
Printing logs in tests is considered harmful - it creates much noise which can hide an actual warning. You only interested in logs of those tests which failed, all the happy ones must keep silent. Normally, this is achieved by adding `logback-test.yaml` or similar configuration file to your test resource folder, in which you set the log level to ERROR or WARNING, thus shutting up all the debug and info printing.

In some cases, however, your code does log at ERROR level legitimately - e.g. in an exception handler.  In `assertThrows` tests you're getting a printout like this which clutters your build log:
```

```

# Solution
Using `@SuppressLogs` annotation you can silence the logging for the duration of the test which executes the error scenario.

# Getting Started

# Dependency

Maven 
```xml
<dependency>
    <groupId>nl.jqno.equalsverifier</groupId>
    <artifactId>equalsverifier</artifactId>
    <version>3.15.3</version>
    <scope>test</scope>
</dependency>
```

Gradle
```groovy
dependencies {
    testImplementation 'ch.qos.logback:logback-classic:1.4.11'
}
```

# Usage

## Parameter injection
```java
@ExtendWith(SuppressLogsExtension.class)
public class SuppressLogsExtensionTest {

    private final BeanWithLogger beanWithLogger = new BeanWithLogger();

    @Test
    void testParameterInjection(@SuppressLogs BeanWithLogger beanWithLogger) {
        assertThrows(Exception.class, beanWithLogger::doSomething);
    }
}
```

Suppress Logs extension will try to find the bean by the following logic:
* field name matched the name of parameter
  * this can be overridden by `fieldName` parameter in the annotation
* field type matches the type of the parameter 


## Method annotation
```java
@ExtendWith(SuppressLogsExtension.class)
public class SuppressLogsExtensionTest {

    private final BeanWithLogger beanWithLogger = new BeanWithLogger();

    @Test
    @SuppressLogs(fieldName = "beanWithLogger")
    void testMethodInjection() {
        assertThrows(Exception.class, beanWithLogger::doSomething);
    }
}
```

In this case `fieldName` parameter is required

# Thread safety 

Note that because loggers are global singletons, changing their loglevel is NOT thread-safe. Your temporary change to the loglevel is effective for all tests which happen to execute that moment in parallel.

If you use Surefire plugin, you can prevent concurrent execution by annotating such tests with `@NotThreadSafe`. More information is found in chapter [Parallel Test Execution and Single Thread Execution](https://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html)

# Contribution
Pull requests are welcome! If you plan to open one, please first create an issue where you describe the problem/gap your contribution closes, and tag the keeper(s) of this repo so they could get back to you with help.
