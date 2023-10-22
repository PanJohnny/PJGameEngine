package com.panjohnny.pjsl.lexer;

import com.panjohnny.pjsl.compiler.Compiler;

import java.util.*;
import java.util.function.Predicate;

public class TokenHolder {
    private List<TokenDummy> list;
    public TokenHolder() {
        list = new LinkedList<>();
    }

    private TokenHolder (List<TokenDummy> list) {
        this.list = list;
    }

    public void append(Token token, String data, int index) {
        list.add(new TokenDummy(token, data, index));
    }

    public void sort() {
        list = list.stream().sorted(Comparator.comparingInt(a -> a.index)).toList();
    }

    public long count(Token type) {
        return list.stream().filter(t -> t.token == type).count();
    }

    public boolean anyMatch(Predicate<TokenDummy> predicate) {
        return list.stream().anyMatch(predicate);
    }

    public Iterator<TokenDummy> iterator(int beginIndex) {
        return list.stream().filter(d -> d.index >= beginIndex).iterator();
    }

    public TokenDummy getMatching(Token type, String begins) throws NullPointerException {
        var first = list.stream().filter(d -> d.token == type && d.raw.startsWith(begins)).findFirst();
        if (first.isEmpty())
            throw new NullPointerException("Matching token not found");
        return first.get();
    }

    public List<TokenDummy> list() {
        return list;
    }

    public static TokenHolder fromString(String s) {
        List<TokenDummy> list = new LinkedList<>();
        String[] arr = Arrays.stream(s.split("[ \n;()]")).filter(f -> !f.isEmpty()).map(String::trim).toArray(String[]::new);
        for (int i = 0; i < arr.length; i++) {
            try {
                Token token = Token.valueOf(arr[i]);
                list.add(new TokenDummy(token, null, i));
            } catch (Exception ignored) {

            }
        }
        return new TokenHolder(list);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        list.forEach(e -> sb.append(e.toString()).append("\n"));
        return sb.toString();
    }

    public record TokenDummy(Token token, String raw, int index) {
        @Override
        public String toString() {
            return token.name() + " " + raw;
        }

        public String second(boolean expectTwo, String... regex) {
            String[] split = raw.split(regex != null&&regex.length>0?regex[0]:"( )");
            if (expectTwo && split.length != 2)
                throw new Compiler.CompilationError("Expected two arguments at: %s, when compiling token: %s".formatted(raw, token.name()));

            return split[1];
        }

        public String between(String a, String b) {
            return raw.substring(raw.indexOf(a) + 1, raw.lastIndexOf(b)).trim();
        }

        public String betweenF(String a, String b) {
            return raw.substring(raw.indexOf(a) + 1, raw.indexOf(b)).trim();
        }
    }
}
