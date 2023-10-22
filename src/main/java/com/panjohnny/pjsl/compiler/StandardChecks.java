package com.panjohnny.pjsl.compiler;

import com.panjohnny.pjsl.lexer.Token;
import com.panjohnny.pjsl.lexer.TokenHolder;

@SuppressWarnings("unused")
public final class StandardChecks {
    @Check("DEPTH_ANALYSIS")
    public static boolean depth(TokenHolder tokenHolder) {
        return tokenHolder.count(Token.IN) == tokenHolder.count(Token.OUT);
    }

    @Check("CLASS_DECLARATION_AT_START")
    public static boolean classDeclareAtStart(TokenHolder tokenHolder) {
        tokenHolder.sort();
        var list = tokenHolder.list();

        Token last = null;
        boolean checkForAny = false;
        for (TokenHolder.TokenDummy tokenDummy : list) {
            if (last == null) {
                if (tokenDummy.token() != Token.CLASS_DECLARATION)
                    return false;
                last = tokenDummy.token();
            } else if (checkForAny && tokenDummy.token() == Token.CLASS_DECLARATION) {
                return false;
            } else {
                if (tokenDummy.token() != Token.CLASS_DECLARATION && !checkForAny)
                    checkForAny = true;
            }
        }
        return true;
    }

    @Check("CORRECT_CLASS_DECLARATIONS")
    public static boolean correctDeclarations(TokenHolder tokenHolder) {
        return tokenHolder.anyMatch(t -> t.raw().startsWith("@type")) && tokenHolder.anyMatch(t -> t.raw().startsWith("@package")) && tokenHolder.anyMatch(t -> t.raw().startsWith("@class"));
    }
}
