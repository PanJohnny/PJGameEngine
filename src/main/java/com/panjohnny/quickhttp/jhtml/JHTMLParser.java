package com.panjohnny.quickhttp.jhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class JHTMLParser {
    private static final System.Logger LOGGER = System.getLogger("JHTMLParser");

    public static String parse(String s, HashMap<String, String> setValues) {
        Document doc = Jsoup.parse(s);
        for (Element jElement : doc.getAllElements()) {

            if (setValues != null && jElement.hasAttr("jhtml:set")) {
                String set = jElement.attr("jhtml:set");
                jElement.html(setValues.getOrDefault(set, "undefined"));
            }

            if (jElement.hasAttr("jhtml:src")) {
                String src = jElement.attr("jhtml:src");
                try {
                    Class<?> clazz = Class.forName(src);

                    if (JHTMLElement.class.isAssignableFrom(clazz)) {
                        clazz.getMethod("createElement", Element.class).invoke(clazz.getDeclaredConstructor().newInstance(), jElement);
                        jElement.attributes().remove("jhtml:src");
                    }
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        String html = doc.html();


        if (setValues != null)
            for (Map.Entry<String, String> entry : setValues.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                html = html.replace("${%s}".formatted(k), v);
            }

        StringBuilder finString = new StringBuilder();

        int lastIn;
        int lastEnd = 0;
        int in = html.indexOf("#{");
        while (in != -1) {
            String sub = html.substring(in);

            int close = sub.indexOf("}");
            if (close == -1) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to resolve end of #{} expression at: {0}", in);
                break;
            }

            String whole = sub.substring(2, close);

            // Class name
            int lastDot = whole.lastIndexOf(".");
            if (lastDot == -1) {
                LOGGER.log(System.Logger.Level.ERROR, "Failed to resolve className of #{} expression: {0}, \n\tat: {1}", whole, in);
                break;
            }

            String clazzName = whole.substring(0, lastDot);
            try {
                Class<?> clazz = Class.forName(clazzName);

                // Get field
                String prop = whole.substring(lastDot + 1);
                if (prop.contains("()")) {
                    Method method = clazz.getMethod(prop.replace("()", ""));
                    Object o = method.invoke(null);
                    finString.append(html, lastEnd, in).append(o);
                } else {
                    Field field = clazz.getField(prop);
                    Object o = field.get(null);
                    finString.append(html, lastEnd, in).append(o);
                }
                lastEnd = in + close + 1;

            } catch (ClassNotFoundException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Class {0} not found #{} expression: {1}, \n\tat: {2}", clazzName, whole, in);
                finString = null;
                break;
            } catch (NoSuchFieldException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Field {3} not found on class {0} #{} expression: {1}, \n\tat: {2}", clazzName, whole, in, whole.substring(lastDot + 1));
                finString = null;
                break;
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Field {3} not accessible on class {0} #{} expression: {1}, \n\tat: {2}", clazzName, whole, in, whole.substring(lastDot + 1));
                finString = null;
                break;
            } catch (NoSuchMethodException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Method {3} not found on class {0} #{} expression: {1}, \n\tat: {2}", clazzName, whole, in, whole.substring(lastDot + 1));
                finString = null;
                break;
            } catch (InvocationTargetException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Method {3} not accessible on class {0} #{} expression: {1}, \n\tat: {2}, make sure that the method is public static", clazzName, whole, in, whole.substring(lastDot + 1));
                finString = null;
                break;
            }

            lastIn = in;
            in = html.indexOf("#{", lastIn + 1);
        }

        if (finString != null) {
            finString.append(html.substring(lastEnd));
        }

        return finString == null ? html : finString.toString();
    }

    public static String parse(File file, HashMap<String, String> setValues) {
        try {
            String content = Files.readString(Path.of(file.getPath()));
            return parse(content, setValues);
        } catch (IOException e) {
            return "Failed to read file";
        }
    }

    public static String parseResource(String resource, HashMap<String, String> setValues) {
        try (InputStream stream = JHTMLParser.class.getResourceAsStream(resource)) {
            if (stream == null)
                return "File not found";
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder b = new StringBuilder();
            int a = reader.read();
            while (a != -1) {
                b.append((char) a);
                a = reader.read();
            }
            return parse(b.toString(), setValues);
        } catch (IOException e) {
            return "Failed to read resource";
        }
    }
}
