/*
 *   Copyright 2023 (C) JBM Assistance ZZP, John Y. Pazekha
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.github.neboskreb.suppress.logs;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SuppressLogsExtension implements TestInstancePostProcessor,
                                              BeforeAllCallback,
                                              ParameterResolver,
                                              BeforeTestExecutionCallback,
                                              AfterTestExecutionCallback,
        InvocationInterceptor
{
    private final Map<String, Worker> executions = new HashMap<>();
    private ParamResolver paramResolver;
    private BeanRegistry registry;


    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getTestClass().orElseThrow(() -> new Exception("Missing test class"));
        registry = new BeanRegistry(clazz);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        paramResolver = new ParamResolver(testInstance, registry);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return paramResolver.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();
        AnnotationSupport.findAnnotation(testMethod, SuppressLogs.class)
                .ifPresent(anno -> resolveMethodAnnotation(extensionContext, anno));
    }

    private void resolveMethodAnnotation(ExtensionContext extensionContext, SuppressLogs anno) {
        Object bean = registry.getMatchingBean(anno, extensionContext.getTestInstance().orElseThrow());

        WrappedLogger logger = new BeanInspector().getLogger(bean);

        Worker worker = new Worker(logger, anno.value());
        worker.before();

        executions.put(extensionContext.getUniqueId(), worker);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (executions.containsKey(extensionContext.getUniqueId())) {
            throw new IllegalStateException("Annotating method and parameter at the same type not allowed. Method " + parameterContext.getDeclaringExecutable());
        }

        ParamResolver.Result target = paramResolver.resolveParameter(parameterContext, extensionContext);
        Worker worker = new Worker(target.logger, target.level);
        worker.before();

        executions.put(extensionContext.getUniqueId(), worker);

        return target.bean;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Worker worker = executions.remove(extensionContext.getUniqueId());
        if (worker != null) {
            worker.after();
        }
    }
}
