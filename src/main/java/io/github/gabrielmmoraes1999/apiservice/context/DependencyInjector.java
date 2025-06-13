package io.github.gabrielmmoraes1999.apiservice.context;

import io.github.gabrielmmoraes1999.apiservice.annotation.Autowired;

import java.lang.reflect.Field;

public class DependencyInjector {

    public static void injectAutowiredDependencies(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object dependency = ApplicationContext.getBean(field.getType());
                field.setAccessible(true);

                try {
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency: " + field.getName(), e);
                }
            }
        }
    }

}
