package com.panjohnny.quickhttp;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class QuickHTTPUtil {
    public static String printStackTraceToString(Throwable t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);

        t.printStackTrace(ps);

        return os.toString(StandardCharsets.UTF_8);
    }

    /**
     * Sends string if content type not set sets it to html
     * @param exchange http exchange to use
     * @param rCode the response code
     * @param str string to send
     * @throws IOException if something goes wrong
     */
    public static void sendString(HttpExchange exchange, int rCode, String str) throws IOException {
        byte[] response = str.getBytes(StandardCharsets.UTF_8);

        if (exchange.getResponseHeaders().getFirst("Content-Type") == null) {
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        }
        exchange.sendResponseHeaders(rCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().close();
    }

    /**
     * Sends file, if content type is not set leaves it blank
     * @param exchange http exchange to use
     * @param rCode the response code
     * @param file file to send
     * @throws IOException if something goes wrong
     */
    public static void sendFile(HttpExchange exchange, int rCode, File file) throws IOException {
        if (!file.exists()) {
            sendString(exchange, 404, "File not found");
            return;
        } else if (!file.isFile()) {
            sendString(exchange, 500, "File is directory");
            return;
        } else if (!file.canRead()) {
            sendString(exchange, 401, "Can't access file");
            return;
        }
        exchange.sendResponseHeaders(rCode, file.length());
        Files.copy(file.toPath(), exchange.getResponseBody());
        exchange.getResponseBody().close();
    }

    public static Map<String, String> parseQuery(String query) {
        if (query == null)
            return Collections.emptyMap();
        if (query.startsWith("?"))
            query = query.substring(1);
        String[] args = query.split("&");
        HashMap<String, String> map = new HashMap<>(args.length);
        for (String arg : args) {
            map.put(arg.split("=")[0], arg.split("=").length > 1 ? arg.split("=")[1] : "");
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Parses url param such as if you have URL /tags/[tag] it would parse the [tag] part. For routing you only use /tags/ <- no [tag]
     * @param originalPath path such as /tags/ that you route to
     * @param uri uri that is requested and the param should be fetched, would be /tags/cat for example.
     * @return string containing the url param
     */
    public static String parseURLParam(String originalPath, URI uri) {
        int indexOf = uri.getPath().indexOf(originalPath);
        if (indexOf < 0)
            throw new IllegalArgumentException("Invalid URL, original path not found");
        return uri.getPath().substring(indexOf + originalPath.length());
    }
}
