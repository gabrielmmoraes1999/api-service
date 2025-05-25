package io.github.gabrielmmoraes1999.apiservice;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.BasicAuthFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.lang.reflect.Method;

public class InitializerServer {

    public static void start(int port) throws Exception {
        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new DispatcherServlet()), "/*");

        for (Class<?> configClass : Functions.getReflections().getTypesAnnotatedWith(Configuration.class)) {
            if (configClass.isAnnotationPresent(EnableWebSecurity.class)) {
                Object configInstance = configClass.getConstructor().newInstance();

                for (Method method : configClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Object bean = method.invoke(configInstance);
                        if (bean instanceof BasicAuthFilter) {
                            context.addFilter(new FilterHolder((BasicAuthFilter) bean), "/*", null);
                        }
                    }
                }
            }
        }

        Server server = new Server(port);
        server.setHandler(context);
        server.start();
        System.out.println("Start service port: 8080");
        server.join();
    }
}
