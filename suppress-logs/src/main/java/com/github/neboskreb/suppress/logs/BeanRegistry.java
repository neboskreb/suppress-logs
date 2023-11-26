package com.github.neboskreb.suppress.logs;

import static org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.helpers.AnnotationHelper.getAllFields;

import com.github.neboskreb.suppress.logs.annotation.SuppressLogs;

import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.beans.PropertyWrapper;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;


class BeanRegistry {

    private final Class<?> clazz;

    private boolean initialized = false;
    private Map<String, Field> fieldsByName;
    private Map<Class<?>, Field> fieldsByType;

    public BeanRegistry(Class<?> clazz) {
        this.clazz = clazz;
    }


    public Object getMatchingBean(Parameter parameter, Object testInstance) {
        Field field = bestMatch(parameter);
        return ReflectionSupport.tryToReadFieldValue(field, testInstance)
                .getOrThrow(e -> new ParameterResolutionException("Failed to get the bean with logger", e));
    }

    public Object getMatchingBean(SuppressLogs anno, Object testInstance) {
        Field field = bestMatch(anno);
        return ReflectionSupport.tryToReadFieldValue(field, testInstance)
                .getOrThrow(e -> new ParameterResolutionException("Failed to get the bean with logger", e));
    }

    private void ensureInitialized() {
        if (initialized) return;

        extractFields();
        initialized = true;
    }

    private void extractFields() {
        Map<Field, PropertyWrapper> fields = getAllFields(clazz);

        fieldsByName = new HashMap<>();
        fieldsByType = new HashMap<>();
        for (Field field : fields.keySet()) {
            fieldsByName.put(field.getName(), field);
            fieldsByType.put(field.getType(), field);
        }
        for (Field field : fields.keySet()) {
            String name = field.getName();
            fieldsByName.put(name, field);
        }
    }

    private Field bestMatch(Parameter parameter) {
        ensureInitialized();

        SuppressLogs anno = parameter.getAnnotation(SuppressLogs.class);

        // 1. First try by name
        final String name;
        if (!anno.fieldName().isEmpty()) {
            name = anno.fieldName();
        } else if (parameter.isNamePresent()) {
            name = parameter.getName();
        } else {
            name = null;
        }

        if (name != null) {
            return fieldsByName.get(name);
        }

        // 2. Name could not be inferred. Try by bean type
        return fieldsByType.get(parameter.getType());
    }

    private Field bestMatch(SuppressLogs anno) {
        ensureInitialized();

        // 1. First try by name
        final String name;
        if (!anno.fieldName().isEmpty()) {
            name = anno.fieldName();
        } else {
            name = null;
        }

        if (name != null) {
            return fieldsByName.get(name);
        }

        // 2. Name could not be inferred. // FIXME what now?
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
