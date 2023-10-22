package com.panjohnny.pjsl.compiler.util;

import com.panjohnny.pjsl.lexer.Token;

public final class SimpleValues {
    public static Object parse(String s, Token type) {
        return switch (type) {
            case CHARACTER -> s.charAt(1); // '1'
            case STRING -> s.substring(1, s.length()-1); // "text"
            case NUMBER -> !s.contains(".")?Integer.parseInt(s):Float.parseFloat(s); // more to come, two types suck
            default -> s;
        };
    }

    public static Class<?> parseType(String s, Token type) {
        return switch (type) {
            case CHARACTER -> char.class; // '1'
            case STRING -> String.class; // "text"
            case NUMBER -> !s.contains(".")?int.class:float.class; // more to come, two types suck
            default -> String.class;
        };
    }
}
