package io.github.gabrielmmoraes1999.apiservice;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielmmoraes1999.apiservice.annotation.*;
import io.github.gabrielmmoraes1999.apiservice.context.DependencyInjector;
import io.github.gabrielmmoraes1999.apiservice.http.ResponseEntity;
import io.github.gabrielmmoraes1999.apiservice.json.JSONObject;
import io.github.gabrielmmoraes1999.apiservice.serializer.*;
import io.github.gabrielmmoraes1999.apiservice.utils.ExceptionUtils;
import io.github.gabrielmmoraes1999.apiservice.utils.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    private final Map<String, Set<String>> registeredPaths;
    private final Map<String, List<RouteInfo>> routes;
    private final ObjectMapper objectMapper;

    public DispatcherServlet() {
        this.registeredPaths = new HashMap<>();
        this.routes = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void init() throws ServletException {
        try {
            objectMapper.registerModule(ConfigSerializer.getSimpleModule());

            for (Class<?> controller : Functions.getClassesWithAnnotation(RestController.class)) {
                addRestController(controller);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String httpMethod = req.getMethod();
        String path = req.getPathInfo();

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter printWriter = resp.getWriter();

        if (path == null) path = "/";

        List<RouteInfo> routeInfos = routes.get(httpMethod);
        if (routeInfos == null) {
            resp.setStatus(405);
            printWriter.write(Message.error("HTTP method not supported").toString());
            return;
        }

        for (RouteInfo routeInfo : routeInfos) {
            Matcher matcher = routeInfo.getPathPattern().matcher(path);
            if (matcher.matches()) {
                try {
                    Map<String, String> pathVariables = new HashMap<>();
                    List<String> pathVariableNames = routeInfo.getPathVariableNames();

                    for (int i = 0; i < pathVariableNames.size(); i++) {
                        pathVariables.put(pathVariableNames.get(i), matcher.group(i + 1));
                    }

                    Object result = invokeMethod(routeInfo.getMethod(), routeInfo.getControllerInstance(), req, resp, pathVariables);

                    if (result != null) {
                        if (result instanceof ResponseEntity) {
                            ResponseEntity<?> entity = (ResponseEntity<?>) result;
                            resp.setStatus(entity.getStatus().value());

                            for (Map.Entry<String, String> header : entity.getHeaders().entrySet()) {
                                resp.setHeader(header.getKey(), header.getValue());
                            }

                            Object body = entity.getBody();
                            if (body instanceof String) {
                                resp.setContentType("application/text;charset=UTF-8");
                                printWriter.write((String) body);
                            } else if (body instanceof JSONObject) {
                                resp.setContentType("application/json;charset=UTF-8");
                                printWriter.write(body.toString());
                            } else {
                                resp.setContentType("application/json;charset=UTF-8");
                                printWriter.write(objectMapper.writeValueAsString(body));
                            }
                        } else if (result instanceof String) {
                            resp.setContentType("application/text;charset=UTF-8");
                            printWriter.write((String) result);
                        } else if (result instanceof JSONObject) {
                            resp.setContentType("application/json;charset=UTF-8");
                            printWriter.write(result.toString());
                        } else {
                            resp.setContentType("application/json;charset=UTF-8");
                            printWriter.write(objectMapper.writeValueAsString(result));
                        }
                    } else {
                        resp.setContentType("application/json;charset=UTF-8");
                        printWriter.write("{}");
                    }
                    return;
                } catch (Exception ex) {
                    resp.setStatus(500);
                    printWriter.write(ExceptionUtils.toJson(ex));
                    return;
                }
            }
        }

        resp.setStatus(404);
        printWriter.write(Message.error("Route not found").toString());
    }

    private Object invokeMethod(Method method, Object controllerInstance,
                                HttpServletRequest req, HttpServletResponse resp,
                                Map<String, String> pathVariables) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, String[]> queryParams = req.getParameterMap();

        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            boolean handled = false;

            for (Annotation ann : p.getAnnotations()) {
                if (ann instanceof RequestBody) {
                    if (Map.class.isAssignableFrom(p.getType())) {
                        args[i] = objectMapper.readValue(req.getReader(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    } else if (JSONObject.class.isAssignableFrom(p.getType())) {
                        StringBuilder sb = new StringBuilder();
                        String line;

                        BufferedReader reader = req.getReader();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        args[i] = new JSONObject(sb.toString());
                    } else if (List.class.isAssignableFrom(p.getType())) {
                        Type genericType = p.getParameterizedType();

                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;

                            Type actualType = parameterizedType.getActualTypeArguments()[0];
                            Class<?> listElementClass = Class.forName(actualType.getTypeName());

                            JavaType javaType = objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class, listElementClass);

                            args[i] = objectMapper.readValue(req.getReader(), javaType);
                        } else {
                            args[i] = objectMapper.readValue(req.getReader(), List.class);
                        }
                    } else {
                        args[i] = objectMapper.readValue(req.getReader(), p.getType());
                    }

                    handled = true;
                    break;
                } else if (ann instanceof PathVariable) {
                    String name = ((PathVariable) ann).value();
                    String val = pathVariables.get(name);
                    args[i] = convertType(p.getType(), val);
                    handled = true;
                    break;
                } else if (ann instanceof RequestParam) {
                    String name = ((RequestParam) ann).value();
                    String[] vals = queryParams.get(name);
                    if (vals != null && vals.length > 0) {
                        args[i] = convertType(p.getType(), vals[0]);
                    } else {
                        args[i] = null;
                    }
                    handled = true;
                    break;
                } else if (ann instanceof RequestHeader) {
                    RequestHeader header = (RequestHeader) ann;

                    if (Map.class.isAssignableFrom(p.getType())) {
                        // Injetar todos os headers
                        Map<String, Object> headersMap = new HashMap<>();
                        Enumeration<String> headerNames = req.getHeaderNames();

                        while (headerNames.hasMoreElements()) {
                            String headerName = headerNames.nextElement();
                            headersMap.put(headerName, req.getHeader(headerName));
                        }

                        args[i] = headersMap;
                        handled = true;
                        break;
                    } else if (JSONObject.class.isAssignableFrom(p.getType())) {
                        JSONObject jsonObject = new JSONObject();
                        Enumeration<String> headerNames = req.getHeaderNames();

                        while (headerNames.hasMoreElements()) {
                            String headerName = headerNames.nextElement();
                            jsonObject.put(headerName, req.getHeader(headerName));
                        }

                        args[i] = jsonObject;
                        handled = true;
                        break;
                    } else {
                        String name = header.value();
                        String headerValue = req.getHeader(name);

                        if (headerValue == null) {
                            if (!header.defaultValue().isEmpty()) {
                                headerValue = header.defaultValue();
                            } else if (header.required()) {
                                throw new RuntimeException("Mandatory header: " + name);
                            }
                        }

                        args[i] = convertType(p.getType(), headerValue);
                        handled = true;
                        break;
                    }
                }
            }

            if (!handled) {
                // Se não tem anotação, tenta injetar request ou response se for tipo compatível
                if (HttpServletRequest.class.isAssignableFrom(p.getType())) {
                    args[i] = req;
                } else if (HttpServletResponse.class.isAssignableFrom(p.getType())) {
                    args[i] = resp;
                } else {
                    args[i] = null; // ou lançar erro
                }
            }
        }

        return method.invoke(controllerInstance, args);
    }

    public void addRestController(Class<?> controller) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ServletException {
        Object controllerInstance = controller.getConstructor().newInstance();
        DependencyInjector.injectAutowiredDependencies(controllerInstance);
        String basePath = "";

        if (controller.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = controller.getAnnotation(RequestMapping.class);
            basePath = requestMapping.value();
        }

        if (!basePath.startsWith("/")) basePath = "/" + basePath;
        if (basePath.endsWith("/")) basePath = basePath.substring(0, basePath.length() - 1);

        for (Method method : controller.getDeclaredMethods()) {
            String httpMethod = null;
            String routePath = null;

            if (method.isAnnotationPresent(GetMapping.class)) {
                httpMethod = "GET";
                routePath = Objects.requireNonNull(method.getAnnotation(GetMapping.class)).value();
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                httpMethod = "POST";
                routePath = Objects.requireNonNull(method.getAnnotation(PostMapping.class)).value();
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                httpMethod = "PUT";
                routePath = Objects.requireNonNull(method.getAnnotation(PutMapping.class)).value();
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                httpMethod = "DELETE";
                routePath = Objects.requireNonNull(method.getAnnotation(DeleteMapping.class)).value();
            }

            if (httpMethod != null && routePath != null) {
                String fullPath = basePath + (routePath.startsWith("/") ? routePath : "/" + routePath);
                RouteInfo routeInfo = RouteInfo.createRouteInfo(method, controllerInstance, fullPath);

                registeredPaths.putIfAbsent(httpMethod, new HashSet<>());
                Set<String> paths = registeredPaths.get(httpMethod);

                if (paths.contains(fullPath)) {
                    throw new ServletException("Duplicate Route: [" + httpMethod + " " + fullPath + "]");
                }
                paths.add(fullPath);

                routes.computeIfAbsent(httpMethod, k -> new ArrayList<>()).add(routeInfo);
                routes.get(httpMethod).sort((r1, r2) -> {
                    int vars1 = r1.getPathVariableNames().size();
                    int vars2 = r2.getPathVariableNames().size();

                    if (vars1 != vars2)
                        return Integer.compare(vars1, vars2);

                    int fixed1 = countFixedSegments(r1);
                    int fixed2 = countFixedSegments(r2);

                    return Integer.compare(fixed2, fixed1);
                });
            }
        }
    }

    private Object convertType(Class<?> type, String value) {
        if (value == null) return null;
        if (type == String.class) return value;
        if (type == Integer.class || type == int.class) return Integer.parseInt(value);
        if (type == Long.class || type == long.class) return Long.parseLong(value);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value);
        if (type == Double.class || type == double.class) return Double.parseDouble(value);
        return null; // pode lançar exceção se quiser
    }

    private int countFixedSegments(RouteInfo routeInfo) {
        String pattern = routeInfo.getPathPattern().pattern();
        Matcher m = Pattern.compile("\\^/(.*)\\$").matcher(pattern);

        if (m.find()) {
            int count = 0;
            for (String part : m.group(1).split("/")) {
                if (!part.equals("([^/]+)")) count++;
            }
            return count;
        }

        return 0;
    }

}
