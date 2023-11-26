package com.github.neboskreb.suppress.logs;

import static java.util.stream.Collectors.toList;

import com.github.neboskreb.suppress.logs.strategy.jdk14.LoggerStrategyFactoryJdk14;
import com.github.neboskreb.suppress.logs.strategy.nop.LoggerStrategyFactoryNop;
import com.github.neboskreb.suppress.logs.strategy.logback.LoggerStrategyFactoryLogback;
import com.github.neboskreb.suppress.logs.strategy.reload4j.LoggerStrategyFactoryReload4j;

import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public class BeanInspector {
    private static final List<LoggerStrategyFactory> supported = List.of(
            new LoggerStrategyFactoryLogback(),
            new LoggerStrategyFactoryReload4j(),
            new LoggerStrategyFactoryJdk14(),
            new LoggerStrategyFactoryNop()
    );

    private static final Predicate<Field> isLoggerCandidate;

    static {
        Predicate<Field> p1 = field -> field.getName().equalsIgnoreCase("LOG");
        Predicate<Field> p2 = field -> field.getName().equalsIgnoreCase("LOGGER");
        isLoggerCandidate = p1.or(p2);
    }


    public WrappedLogger getLogger(Object instance) {
        return toLoggerStrategy(getActualLogger(instance));
    }

    private Object getActualLogger(Object instance) {
        List<Field> candidates = ReflectionSupport.findFields(instance.getClass(), isLoggerCandidate, HierarchyTraversalMode.TOP_DOWN);
        switch (candidates.size()) {
            case 1:
                Try<Object> result = ReflectionSupport.tryToReadFieldValue(candidates.get(0), instance);
                return result.getOrThrow(e -> new ParameterResolutionException("Failed to get logger from the bean", e));
            case 0:
                throw new IllegalStateException("No loggers found in bean " + instance);
            default:
                throw new IllegalStateException("Too many loggers found in bean " + instance + ": " +
                        candidates.stream().map(Field::getName).collect(toList()));
        }
    }

    private WrappedLogger toLoggerStrategy(Object actualLogger) {
        return supported.stream()
                .filter(LoggerStrategyFactory::isAvailable)
                .filter(factory -> factory.canWrap(actualLogger))
                .findAny()
                .map(factory -> factory.wrap(actualLogger))
                .orElseThrow(() -> new UnsupportedOperationException("Logger not (yet) supported: "
                        + actualLogger.getClass().getSimpleName()));
    }

}
