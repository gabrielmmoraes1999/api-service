package io.github.gabrielmmoraes1999.apiservice;

import org.eclipse.jetty.util.annotation.Name;

import java.util.Objects;

public class ApplicationServer {

    private static ManagerServer managerServer;

    public static void run() throws Exception {
        managerServer = new ManagerServer();
        managerServer.start();
    }

    public static void run(Class<?> appClass) throws Exception {
        managerServer = new ManagerServer(appClass);
        managerServer.start();
    }

    public static void run(Class<?> appClass, @Name("port") int port) throws Exception {
        managerServer = new ManagerServer(appClass, port);
        managerServer.start();
    }

    public static void run(@Name("port") int port) throws Exception {
        managerServer = new ManagerServer(port);
        managerServer.start();
    }

    public static void stop() throws Exception {
        if (Objects.nonNull(managerServer)) {
            managerServer.stop();
        }
    }
}
