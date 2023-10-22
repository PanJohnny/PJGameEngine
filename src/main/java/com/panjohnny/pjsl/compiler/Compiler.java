package com.panjohnny.pjsl.compiler;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.panjohnny.pjsl.compiler.util.AbstractSyntaxTree;
import com.panjohnny.pjsl.compiler.util.SimpleValues;
import com.panjohnny.pjsl.lexer.Token;
import com.panjohnny.pjsl.lexer.TokenHolder;
import com.panjohnny.pjsl.util.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Compiler {
    private final TokenHolder tokens;
    private final System.Logger logger;

    public Compiler(TokenHolder tokens) {
        this.tokens = tokens;
        this.logger = System.getLogger("PJSL");
    }

    public boolean runChecks() {
        logger.log(System.Logger.Level.INFO, "==== PJSL Checks ====");
        int failed = 0;
        int passed = 0;
        for (Method method : Arrays.stream(StandardChecks.class.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Check.class)).toList()) {
            Check c = method.getAnnotation(Check.class);
            try {
                boolean b = (boolean) method.invoke(null, tokens);
                if (b) {
                    logger.log(System.Logger.Level.INFO, c.value() + " " + Ansi.colorize(" SUCCESS ", Attribute.BRIGHT_GREEN_BACK(), Attribute.BRIGHT_WHITE_TEXT()));
                    passed++;
                } else {
                    logger.log(System.Logger.Level.INFO, c.value() + " " + Ansi.colorize(" ERROR ", Attribute.RED_BACK(), Attribute.BRIGHT_WHITE_TEXT()));
                    failed++;
                }
            } catch (Exception e) {
                logger.log(System.Logger.Level.INFO, c.value() + " " + Ansi.colorize(" FAILED ", Attribute.BRIGHT_YELLOW_BACK(), Attribute.BLACK_TEXT()));
                failed++;
            }
        }

        if (failed != 0) {
            logger.log(System.Logger.Level.ERROR, "Checks failed! ({0}/{1})", failed, failed+passed);
            return false;
        }

        logger.log(System.Logger.Level.INFO, Ansi.colorize(" Checks passed! ", Attribute.BRIGHT_GREEN_BACK(), Attribute.BRIGHT_WHITE_TEXT()) + " ({0})", passed);
        return true;
    }

    public void compileSafe() {
        try {
            compile();
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR, "Compilation failed");
            logger.log(System.Logger.Level.ERROR, e);
        }
    }

    public void compile() throws ClassNotFoundException {
        if (!runChecks())
            throw new CompilationError("Some checks failed");

        logger.log(System.Logger.Level.INFO, "Attempting to compile...\n{0}", tokens);

        AbstractSyntaxTree tree = new AbstractSyntaxTree();

        String className = tokens.getMatching(Token.CLASS_DECLARATION, "@class").second(true);
        String typeName = tokens.getMatching(Token.CLASS_DECLARATION, "@type").second(true);
        String packageName = tokens.getMatching(Token.CLASS_DECLARATION, "@package").second(true);

        tree.write("package", packageName);
        AbstractSyntaxTree.Visitor v = tree.visitChild("class");
        v.write("access", "public");
        v.write("type", Class.forName(typeName));
        v.write("name", className);
        tree.visitChild("body");

        {
            // Define components
            Iterator<TokenHolder.TokenDummy> tk = tokens.iterator(0);

            for (int i = 0; tk.hasNext(); i++) {
                TokenHolder.TokenDummy next = tk.next();
                if (next.token() == Token.DEFINE_COMPONENT) {
                    String type = next.betweenF(" ", "(");
                    String[] args = StringUtil.commaSeparated(next.between("(", ")")); // Component(Type, Type)
                    List<String> values = new ArrayList<>();
                    int off = 0;
                    for (String a : args) {
                        Token argType = Token.valueOf(a);

                        Iterator<TokenHolder.TokenDummy> iterator = tokens.iterator(i + off);
                        while (iterator.hasNext()) {
                            TokenHolder.TokenDummy argVal = iterator.next();
                            if (argVal.token() == argType) {
                                values.add(argVal.raw());
                                off++;
                                break;
                            }
                        }
                    }
                    logger.log(System.Logger.Level.INFO, "Component {0} params {1} types {2}", type, values, Arrays.toString(args));
                    AbstractSyntaxTree.Visitor c = tree.visitChild("define");
                    c.write("access", "public");
                    c.write("type", Class.forName("com.panjohnny.pjgl.api.object.components." + type));
                    AbstractSyntaxTree.Visitor init = c.visitChild("init");
                    for (int j = 0; j < values.size(); j++) {
                        String value = values.get(j);
                        AbstractSyntaxTree.Visitor arg = init.visitChild("arg");
                        Token token = Token.valueOf(args[j]);
                        arg.write("type", SimpleValues.parseType(value, token));
                        arg.write("value", SimpleValues.parse(value, token));
                    }
                    tree.visitParent();
                }
            }
        }



        logger.log(System.Logger.Level.INFO, "Tree: {0}", tree);
    }

    public static class CompilationError extends Error {
        public CompilationError(String message) {
            super(message);
        }
    }
}
