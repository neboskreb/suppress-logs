package com.github.neboskreb.suppress.logs;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;

class ParamResolver implements ParameterResolver {
    public static class Result {
        public final Object bean;
        public final WrappedLogger logger;
        public final String level;

        public Result(Object bean, WrappedLogger logger, String level) {
            this.bean = bean;
            this.logger = logger;
            this.level = level;
        }
    }


    private final Object testInstance;
    private final BeanRegistry registry;

    ParamResolver(Object testInstance, BeanRegistry registry) {
        this.testInstance = testInstance;
        this.registry = registry;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getAnnotation(SuppressLogs.class) != null;
    }

    @Override
    public Result resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();

        Object bean = registry.getMatchingBean(parameter, testInstance);

        WrappedLogger logger = new BeanInspector().getLogger(bean);

        String level = parameter.getAnnotation(SuppressLogs.class).value();

        return new Result(bean, logger, level);
    }
}
