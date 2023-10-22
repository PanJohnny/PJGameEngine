package com.panjohnny.pjsl.compiler.util;

import java.util.LinkedList;

// Here we go again
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class AbstractSyntaxTree {
    private final Visitor root;
    private Visitor context;
    public AbstractSyntaxTree() {
        root = new Visitor("<ROOT>", this, null);
        context = root;
    }

    public Visitor visitParent() {
        context = context.visitParent();
        return context;
    }

    public Visitor visitRoot() {
        context = context.visitRoot();
        return context;
    }

    public Visitor visitChild(Object data) {
        context = context.visitChild(data);
        return context;
    }

    public void write(String key, Object value) {
        context.write(key, value);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static class Visitor {
        private final Object data;
        private final AbstractSyntaxTree tree;
        private final LinkedList<Visitor> children;
        private final Visitor parent;

        public Visitor(Object data, AbstractSyntaxTree tree, Visitor parent) {
            this.data = data;
            this.tree = tree;
            this.children = new LinkedList<>();
            this.parent = parent;
        }

        public Visitor visitRoot() {
            return tree.root;
        }

        public Visitor visitParent() {
            return parent;
        }
        public Visitor visitChild(Object data) {
            Visitor v = new Visitor(data, tree, this);
            children.add(v);
            return v;
        }

        public void write(String key, Object value) {
            this.visitChild(key).visitChild(value);
        }

        @Override
        public String toString() {
            return toString(0);
        }

        protected String toString(int depth) {
            StringBuilder sb = new StringBuilder();
            sb.append("---".repeat(Math.max(0, depth)));
            sb.append(data).append("\n");
            depth++;

            for (Visitor child : children) {
                sb.append(child.toString(depth));
            }

            return sb.toString();
        }
    }
}
