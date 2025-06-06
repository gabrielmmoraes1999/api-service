package io.github.gabrielmmoraes1999.apiservice.context;

import io.github.gabrielmmoraes1999.apiservice.Functions;
import io.github.gabrielmmoraes1999.apiservice.annotation.Autowired;
import io.github.gabrielmmoraes1999.apiservice.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

    private static final Map<Class<?>, Object> beans = new HashMap<>();

    public static void init(Class<?> appClass) {
        if (!appClass.isAnnotationPresent(ComponentScan.class)) {
            throw new RuntimeException("Missing @ComponentScan on main class");
        }

        Functions.setPackages(appClass.getAnnotation(ComponentScan.class).basePackages());
        ApplicationContext.init();
    }

    public static void init() {
        for (Class<?> clazz : Functions.getClassesWithAnnotationPresent(Autowired.class)) {
            try {
                beans.put(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Error instance bean: " + clazz.getName(), e);
            }
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return clazz.cast(beans.get(clazz));
    }
}
