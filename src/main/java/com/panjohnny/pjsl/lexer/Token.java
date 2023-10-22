package com.panjohnny.pjsl.lexer;

import java.util.regex.Pattern;

public enum Token {

    NUMBER("(\\d+)((_\\d+)+)?(\\.\\d+)?"),
    STRING("\\\".+[^\\n]+\\\""),
    CHARACTER("'[^']'"),
    COMMENT("//.+[^\n]"),
    CLASS_DECLARATION("((@package)|(@class)|(@type)) [\\w\\.]+"),
    DEFINE_COMPONENT("component \\w+\\(.+\\)( as \\w+)?"),
    SUPER_INVOKE("super\\.\\w+\\((.+[^\\)])?\\)"),
    INVOKE("(@)?\\w+\\.\\w+\\((.+[^\\)])?\\)"),
    FUNCTION("function \\w+\\((.+[^\\)])?\\)( +)?:( +)?\\w+"),
    ANNOTATION("@\\w+"),
    IMPORT("import (static )?(#)?\\w+((\\.[\\w\\*]+)+)?"),
    FIELD_ACCESS("\\w+(\\.\\w+)+"),
    DEFINE("define( \\w+)(( +)?:( +)?\\w+)?( +)?=.+"),
    SET_VALUE("(\\w+ )?[\\+\\-\\*\\/]?=.+"),
    LOGIC("((if)|(else)|(for)|(while))"),
    IN("\\{"),
    OUT("\\}");

    final Pattern expect;
    Token(String expect) {
        this.expect = Pattern.compile(expect, Pattern.MULTILINE & Pattern.DOTALL);
    }

    public String matchAllAndTokenize(String source) {
        if (this == COMMENT)
            return source.replaceAll(expect.pattern(), "");
        return source.replaceAll(expect.pattern(), name());
    }

    public String matchAllAndTokenize(String source, TokenHolder holder) {
        return expect.matcher(source).replaceAll((result) -> {
            if (this == COMMENT)
                return "";
            holder.append(this, result.group(), result.start());
            return name();
        });
    }
}
