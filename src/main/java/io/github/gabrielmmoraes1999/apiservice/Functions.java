package io.github.gabrielmmoraes1999.apiservice;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Functions {

    public static List<Class<?>> getClasse(Class<? extends Annotation> annotationClass) {
        List<Class<?>> result = new ArrayList<>();

        for (Class<?> clazz : Functions.getReflections().getSubTypesOf(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                Class<?> classType = field.getType();
                if (field.isAnnotationPresent(annotationClass) && !result.contains(classType)) {
                    result.add(classType);
                }
            }
        }

        return result;
    }

    public static Reflections getReflections() {
        return new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(Functions.getPackager()))
                        //.addUrls(ClasspathHelper.forClassLoader())
                        .setScanners(
                                new SubTypesScanner(false),
                                new FieldAnnotationsScanner(),
                                new TypeAnnotationsScanner()
                        )
        );
    }

    public static String getPackager() {
        Thread mainThread = Thread.currentThread();
        StackTraceElement[] stackTrace = mainThread.getStackTrace();

        try {
            for (StackTraceElement element : stackTrace) {
                if (element.getMethodName().equals("main")) {
                    return Class.forName(element.getClassName()).getPackage().getName();
                }
            }

            return null;
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

}
