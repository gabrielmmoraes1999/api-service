package io.github.gabrielmmoraes1999.apiservice;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Component;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.*;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.controller.OAuth2TokenController;
import io.github.gabrielmmoraes1999.apiservice.serializer.ConfigSerializer;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.annotation.Name;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

public class ManagerServer extends Server {

    protected ManagerServer() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(Integer.parseInt(System.getProperty("server.port")));

        ConfigSerializer.init();
        ApplicationContext.init();
        this.init();
    }

    protected ManagerServer(Class<?> appClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(Integer.parseInt(System.getProperty("server.port")));

        ConfigSerializer.init();
        ApplicationContext.init(appClass);
        this.init();
    }

    protected ManagerServer(Class<?> appClass, @Name("port") int port) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(port);

        ConfigSerializer.init();
        ApplicationContext.init(appClass);
        this.init();
    }

    protected ManagerServer(@Name("port") int port) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(port);

        ConfigSerializer.init();
        ApplicationContext.init();
        this.init();
    }

    private void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(dispatcherServlet), "/*");

        for (Class<?> clazz : Functions.getClassesWithAnnotation(Component.class)) {
            Object configInstance = clazz.getConstructor().newInstance();

            if (Filter.class.isAssignableFrom(clazz)) {
                FilterHolder filterHolder = new FilterHolder((Filter) configInstance);
                context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    method.invoke(configInstance);
                }
            }
        }

        for (Class<?> configClass : Functions.getClassesWithAnnotation(Configuration.class)) {
            if (configClass.isAnnotationPresent(EnableWebSecurity.class)) {
                Object configInstance = configClass.getConstructor().newInstance();

                for (Method method : configClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Class<?> classReturn = method.getReturnType();

                        if (classReturn == SecurityFilterChain.class) {
                            method.invoke(configInstance, new HttpSecurity());
                        } else if (classReturn == JwtAuthFilter.class) {
                            Object bean = method.invoke(configInstance);

                            try {
                                dispatcherServlet.addRestController(OAuth2TokenController.class);
                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                                     IllegalAccessException | ServletException ex) {
                                throw new RuntimeException(ex);
                            }

                            context.addFilter(new FilterHolder((JwtAuthFilter) bean), "/*", EnumSet.of(DispatcherType.REQUEST));
                        } else if (classReturn ==  BasicAuthFilter.class) {
                            Object bean = method.invoke(configInstance);
                            context.addFilter(new FilterHolder((BasicAuthFilter) bean), "/*", null);
                        } else {
                            method.invoke(configInstance);
                        }
                    }
                }
            }
        }

        setHandler(context);
    }

}
