package com.panjohnny.quickhttp.response;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class QuickResponseBuilder {
    private final HttpExchange exchange;
    private final SimpleHtmlElement rootElement;
    private final SimpleHtmlElement body;
    private final SimpleHtmlElement head;
    public QuickResponseBuilder(HttpExchange exchange) {
        this.exchange = exchange;
        this.rootElement = new SimpleHtmlElement("html", null);
        body = this.rootElement.appendChild(new SimpleHtmlElement("body", rootElement));
        head = this.rootElement.appendChild(new SimpleHtmlElement("head", rootElement));
    }

    public QuickResponseBuilder head(Consumer<SimpleHtmlElement> consumer) {
        consumer.accept(head);
        return this;
    }

    public QuickResponseBuilder body(Consumer<SimpleHtmlElement> consumer) {
        consumer.accept(body);
        return this;
    }

    public QuickResponseBuilder header(Consumer<SimpleHtmlElement> consumer) {
        consumer.accept(body.getFirstChildByTagNameOrCreateNew("header"));
        return this;
    }

    public QuickResponseBuilder main(Consumer<SimpleHtmlElement> consumer) {
        consumer.accept(body.getFirstChildByTagNameOrCreateNew("main"));
        return this;
    }

    public QuickResponseBuilder footer(Consumer<SimpleHtmlElement> consumer) {
        consumer.accept(body.getFirstChildByTagNameOrCreateNew("footer"));
        return this;
    }

    public void finishAndSend(Consumer<HttpExchange> lastTouches, int rCode) throws IOException {
        if (lastTouches != null) {
            lastTouches.accept(exchange);
        }
        byte[] response = ("<!DOCTYPE html>\n" + rootElement.toString()).getBytes(StandardCharsets.UTF_8);

        if (exchange.getResponseHeaders().getFirst("Content-Type") == null) {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        }
        exchange.sendResponseHeaders(rCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().close();
    }
}
