package io.github.gabrielmmoraes1999.apiservice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RouteInfo {

    private final Method method;
    private final Object controllerInstance;
    private final Pattern pathPattern;
    private final List<String> pathVariableNames;

     private RouteInfo(Method method, Object controllerInstance, Pattern pathPattern, List<String> pathVariableNames) {
        this.method = method;
        this.controllerInstance = controllerInstance;
        this.pathPattern = pathPattern;
        this.pathVariableNames = pathVariableNames;
    }

    public Method getMethod() {
        return method;
    }

    public Object getControllerInstance() {
        return controllerInstance;
    }

    public Pattern getPathPattern() {
        return pathPattern;
    }

    public List<String> getPathVariableNames() {
        return pathVariableNames;
    }

    public static RouteInfo createRouteInfo(Method method, Object controllerInstance, String fullPath) {
        List<String> pathVariableNames = new ArrayList<>();
        Pattern pathPattern = AntPatterns.compileRoute(fullPath, pathVariableNames);
        return new RouteInfo(method, controllerInstance, pathPattern, pathVariableNames);
    }
}
