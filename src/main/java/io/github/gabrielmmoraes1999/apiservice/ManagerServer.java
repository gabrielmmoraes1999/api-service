package io.github.gabrielmmoraes1999.apiservice;

import io.github.gabrielmmoraes1999.apiservice.annotation.Bean;
import io.github.gabrielmmoraes1999.apiservice.annotation.Configuration;
import io.github.gabrielmmoraes1999.apiservice.annotation.EnableWebSecurity;
import io.github.gabrielmmoraes1999.apiservice.auth.BasicAuthFilter;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.serializer.ConfigSerializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.annotation.Name;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new DispatcherServlet()), "/*");

        for (Class<?> configClass : Functions.getClassesWithAnnotation(Configuration.class)) {
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

        setHandler(context);
    }

}
