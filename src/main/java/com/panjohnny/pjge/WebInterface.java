package com.panjohnny.pjge;

import com.panjohnny.quickhttp.QuickHTTPUtil;
import com.panjohnny.quickhttp.Route;
import com.panjohnny.quickhttp.jhtml.JHTMLParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;

@SuppressWarnings("unused")
public class WebInterface {
    @Route("/index")
    public void index(HttpExchange ex) throws IOException {
        String s = JHTMLParser.parseResource("/index.jhtml", null);
        QuickHTTPUtil.sendString(ex, 200, s);
    }

    @Route("/test/")
    public void test(HttpExchange ex) throws IOException {
        QuickHTTPUtil.sendString(ex, 200, "Test");
    }

    @Route("/app")
    public void app(HttpExchange ex) throws IOException {
        String s = JHTMLParser.parse(new File("./app.jhtml"), null);
        QuickHTTPUtil.sendString(ex, 200, s);
    }

    @Route("/lib")
    public void serveLibrary(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath().split("/lib/")[1];
        if (path.contains("..")) {
            QuickHTTPUtil.sendString(ex, 401, "Why?");
            return;
        }

        ex.getResponseHeaders().set("Content-Type", "text/javascript; charset=UTF-8");
        try (InputStream stream = JHTMLParser.class.getResourceAsStream("/lib/" + path)) {
            if (stream == null) {
                QuickHTTPUtil.sendString(ex, 404, "Not found");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder b = new StringBuilder();
            int a = reader.read();
            while (a != -1) {
                b.append((char) a);
                a = reader.read();
            }
            QuickHTTPUtil.sendString(ex, 200, b.toString());
        } catch (IOException e) {
            QuickHTTPUtil.sendString(ex, 500, "Failed to read");
        }
    }
}
