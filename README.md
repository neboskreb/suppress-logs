# Suppress Logs extension
Extension for JUnit 5/6 and SLF4J for temporary suppressing the error logging when error is expected, e.g. in negative tests.

# What is the problem
Printing logs in tests is considered harmful - it creates much noise which can hide an actual warning. You only interested in logs of those tests which failed, all the happy ones must keep silent. Normally, this is achieved by adding `logback-test.yaml` or similar configuration file to your test resource folder, in which you set the log level to ERROR or WARNING, thus shutting up all the debug and info printing.

In some cases, however, your code does log at ERROR level legitimately - e.g. in an exception handler.  In `assertThrows` tests you're getting a printout like this which clutters your build log:
```
Executing tasks: [:suppress-logs:test, --tests, com.github.neboskreb.suppress.logs.SuppressLogsExtensionTest.testMethodInjection] in project C:\Users\admin\Desktop\CONTRIB\log-level-control


> Task :suppress-logs:compileJava UP-TO-DATE
> Task :suppress-logs:processResources NO-SOURCE
> Task :suppress-logs:classes UP-TO-DATE
> Task :suppress-logs:compileTestJava
> Task :suppress-logs:processTestResources NO-SOURCE
> Task :suppress-logs:testClasses
> Task :suppress-logs:test
18:03:26.600 [Test worker] ERROR com.github.neboskreb.suppress.logs.BeanWithLogger -- Boom!
java.lang.Exception: You're dead!
	at com.github.neboskreb.suppress.logs.BeanWithLogger.doSomething(BeanWithLogger.java:10)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3115)
	at com.github.neboskreb.suppress.logs.SuppressLogsExtensionTest.testMethodInjection(SuppressLogsExtensionTest.java:23)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:728)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.api.extension.InvocationInterceptor.interceptTestMethod(InvocationInterceptor.java:119)
	at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(InterceptingExecutableInvoker.java:103)
	at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker.lambda$invoke$0(InterceptingExecutableInvoker.java:93)
	...

Deprecated Gradle features were used in this build, making it incompatible with Gradle 9.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

For more on this, please refer to https://docs.gradle.org/8.4/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.
BUILD SUCCESSFUL in 2s
```

In your case, the exception log is useless because you know that the exception is expected because _you_ provoke it in your test. But you can't set loglevel to OFF in your `logback-test.yaml` because it will silence also actual warnings which you don't want to miss. It would be nice to silence the logging just for this one test.

# Solution
Using `@SuppressLogs` annotation you can silence the logging for the duration of the test which executes the error scenario.

# Getting Started

# Dependency

Maven 
```xml
<dependency>
    <groupId>io.github.neboskreb</groupId>
    <artifactId>suppress-logs</artifactId>
    <version>1.2</version>
    <scope>test</scope>
</dependency>
```

Gradle
```groovy
dependencies {
    testImplementation 'io.github.neboskreb:suppress-logs:1.2'
}
```

# Usage

Suppress Logs will find the bean under test and inspect its internals to find the logger instance. Before the test, it will set that logger to the loglevel you provided in the annotation (or "OFF" by default) and restore the original loglevel after the test finishes.

Instances of logger are looked up by the field names:
* `LOG`
* `log`
* `LOGGER`
* `logger`

Only one logger instance in the bean is expected.

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
