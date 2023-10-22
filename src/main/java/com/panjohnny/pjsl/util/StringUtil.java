package com.panjohnny.pjsl.util;

@SuppressWarnings("unused")
public final class StringUtil {
    public static String camelCase(String clazz) {
        if (clazz.contains("."))
            clazz = clazz.substring(clazz.lastIndexOf("."));

        return String.valueOf(clazz.charAt(0)).toLowerCase() + clazz.substring(1);
    }

    public static String[] commaSeparated(String s) {
        if (!s.contains(","))
            return new String[] {s.trim()};
        return s.replace(" ", "").split(",");
    }
}
