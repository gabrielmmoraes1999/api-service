package io.github.gabrielmmoraes1999.apiservice;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Functions {

    private static String[] packages = new String[]{""};
    private static Class<?>[] classes = new Class[]{};

    public static List<Class<?>> getClassesWithAnnotationPresent(Class<? extends Annotation> annotationClass) {
        List<Class<?>> result = new ArrayList<>();

        if (classes.length > 0) {
            for (Class<?> clazz : classes) {
                try {
                    for (Field field : clazz.getDeclaredFields()) {
                        Class<?> classType = field.getType();
                        if (field.isAnnotationPresent(annotationClass) && !result.contains(classType)) {
                            result.add(classType);
                        }
                    }
                } catch (Exception | NoClassDefFoundError ignore) {

                }
            }
        } else {
            try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packages).scan()) {
                for (ClassInfo classInfo : scanResult.getAllClasses()) {
                    try {
                        Class<?> clazz = classInfo.loadClass();

                        for (Field field : clazz.getDeclaredFields()) {
                            Class<?> classType = field.getType();
                            if (field.isAnnotationPresent(annotationClass) && !result.contains(classType)) {
                                result.add(classType);
                            }
                        }
                    } catch (Exception | NoClassDefFoundError ignore) {

                    }
                }
            }
        }

        return result;
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        List<Class<?>> classList = new ArrayList<>();

        if (classes.length > 0) {
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(annotation)) {
                    classList.add(clazz);
                }
            }
        } else {
            try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packages).scan()) {
                for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(annotation.getName())) {
                    classList.add(classInfo.loadClass());
                }
            }
        }

        return classList;
    }

    public static void setPackages(String[] packages) {
        Functions.packages = packages;
    }

    public static void setClasses(Class<?>[] classes) {
        Functions.classes = classes;
    }

}
