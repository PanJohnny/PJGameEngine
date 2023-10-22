package com.panjohnny.pjsl.lexer;

import com.panjohnny.pjgl.api.util.Pair;

@SuppressWarnings("unused")
public final class Lexer {
    public static Pair<String, TokenHolder> tokenize(String input) {
        TokenHolder tokenHolder = new TokenHolder();
        for (Token token : Token.values()) {
            input = token.matchAllAndTokenize(input, tokenHolder);
        }
        tokenHolder.sort();
        return new Pair<>(input, tokenHolder);
    }

    public static Pair<String, TokenHolder> tokenizeSimple(String input) {
        for (Token token : Token.values()) {
            input = token.matchAllAndTokenize(input);
        }
        return new Pair<>(input, TokenHolder.fromString(input));
    }
}
