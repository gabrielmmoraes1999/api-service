package io.github.gabrielmmoraes1999.apiservice;

import io.github.gabrielmmoraes1999.apiservice.annotation.*;
import io.github.gabrielmmoraes1999.apiservice.auth.*;
import io.github.gabrielmmoraes1999.apiservice.context.ApplicationContext;
import io.github.gabrielmmoraes1999.apiservice.controller.OAuth2TokenController;
import io.github.gabrielmmoraes1999.apiservice.security.crypto.PasswordEncoder;
import io.github.gabrielmmoraes1999.apiservice.security.jwt.ProviderJwt;
import io.github.gabrielmmoraes1999.apiservice.serializer.ConfigSerializer;
import io.github.gabrielmmoraes1999.apiservice.security.web.HttpSecurity;
import io.github.gabrielmmoraes1999.apiservice.security.web.SecurityFilterChain;
import io.github.gabrielmmoraes1999.apiservice.ssl.SslProperties;
import io.github.gabrielmmoraes1999.apiservice.websocket.annotation.WebSocketConfigurer;
import io.github.gabrielmmoraes1999.apiservice.websocket.model.WebSocketHandlerRegistrationImpl;
import io.github.gabrielmmoraes1999.apiservice.websocket.model.WebSocketHandlerRegistryImpl;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.TimeZone;

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
            } else if (ProviderJwt.class.isAssignableFrom(clazz)) {
                ApplicationContext.addBean(ProviderJwt.class, configInstance);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Bean.class)) {
                    method.invoke(configInstance);
                }
            }
        }

        for (Class<?> configClass : Functions.getClassesWithAnnotation(Configuration.class)) {
            Object configInstance = configClass.getConstructor().newInstance();

            if (configClass.isAnnotationPresent(EnableWebSecurity.class)) {
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
            } else if (configClass.isAnnotationPresent(EnableWebSocket.class)) {
                if (WebSocketConfigurer.class.isAssignableFrom(configClass)) {
                    setWebSocketConfigurer((WebSocketConfigurer) configInstance, context);
                }
            } else {
                for (Method method : configClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Bean.class)) {
                        Class<?> classReturn = method.getReturnType();

                        if (classReturn == SslProperties.class) {
                            SslProperties sslProperties = (SslProperties) method.invoke(configInstance);
                            if (sslProperties.isEnabled()) {
                                SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
                                sslContextFactory.setKeyStore(sslProperties.getKeyStore());
                                sslContextFactory.setKeyStorePassword(sslProperties.getKeyStorePassword());
                                sslContextFactory.setKeyManagerPassword(sslProperties.getKeyPassword());

                                HttpConfiguration httpsConfig = new HttpConfiguration();
                                httpsConfig.setSecureScheme("https");
                                httpsConfig.setSecurePort(sslProperties.getPort());
                                httpsConfig.addCustomizer(new SecureRequestCustomizer());

                                ServerConnector httpsConnector = new ServerConnector(
                                        this,
                                        new SslConnectionFactory(sslContextFactory, "http/1.1"),
                                        new HttpConnectionFactory(httpsConfig)
                                );

                                httpsConnector.setPort(sslProperties.getPort());
                                setConnectors(new Connector[]{httpsConnector});
                            }
                        } else if (classReturn == PasswordEncoder.class) {
                            ApplicationContext.addBean(PasswordEncoder.class, method.invoke(configInstance));
                        } else if (classReturn == TimeZone.class) {
                            ConfigSerializer.setTimeZone((TimeZone) method.invoke(configInstance));
                        } else {
                            method.invoke(configInstance);
                        }
                    }
                }
            }
        }

        setHandler(context);
    }

    private void setWebSocketConfigurer(WebSocketConfigurer webSocketConfigurer, ServletContextHandler servletContextHandler) {
        WebSocketHandlerRegistrationImpl registration = new WebSocketHandlerRegistrationImpl(servletContextHandler);
        WebSocketHandlerRegistryImpl registry = new WebSocketHandlerRegistryImpl(registration);
        webSocketConfigurer.registerWebSocketHandlers(registry);
    }
}
