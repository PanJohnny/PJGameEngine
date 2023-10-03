package com.panjohnny.quickhttp;

import com.panjohnny.quickhttp.response.QuickResponseBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class Router {
    private final HttpServer httpServer;

    public Router(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public void route(String route, HttpHandler handler) {
        httpServer.createContext(route, handler);
    }

    public void removeRoute(String route) {
        httpServer.removeContext(route);
    }

    public void loadFromAnnotations(Object a) {
        for (Method declaredMethod : a.getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(Route.class) && declaredMethod.getParameterCount() == 1
                    && declaredMethod.getParameters()[0].getType().isAssignableFrom(HttpExchange.class)
                    && declaredMethod.getReturnType() == void.class) {
                if (!declaredMethod.canAccess(a)) {
                    throw new RouterAccessError("Router can't access route method " + declaredMethod + ". Please check if the method is public as well as the class.");
                }
                Route annotation = declaredMethod.getAnnotation(Route.class);
                HttpHandler handler = (exchange -> {
                    try {
                        declaredMethod.invoke(a, exchange);
                    } catch (Exception e) {
                        e.printStackTrace();

                        QuickResponseBuilder responseBuilder = new QuickResponseBuilder(exchange);
                        responseBuilder.header(header -> header.textChild("h1", "An internal error occurred")).main(main -> main.child("code").preStr(QuickHTTPUtil.printStackTraceToString(e))).finishAndSend(null, 500);
                    }
                });

                route(annotation.value(), handler);
            }
        }
    }

    static class RouterAccessError extends Error {
        public RouterAccessError(String message) {
            super(message);
        }
    }
}
