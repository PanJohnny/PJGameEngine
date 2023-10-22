package com.panjohnny.test;

import com.panjohnny.pjsl.lexer.Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import com.panjohnny.pjsl.compiler.Compiler;

public class LanguageTester {
    public static void main(String[] args)  {
        try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(LanguageTester.class.getResourceAsStream("/test.pjsl"))))) {
            StringBuilder s = new StringBuilder();
            for (var l = reader.read(); l != -1;l = reader.read())
                s.append((char) l);

            // var tokenized = Lexer.tokenize(s.toString());

            var simple = Lexer.tokenize(s.toString());
            var compiler = new Compiler(simple.b());
            compiler.compileSafe();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
