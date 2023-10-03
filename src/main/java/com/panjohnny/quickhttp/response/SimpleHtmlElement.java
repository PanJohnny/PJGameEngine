package com.panjohnny.quickhttp.response;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("unused")
public class SimpleHtmlElement {
    private final String tagName;
    private final Properties attributes;
    private final List<SimpleHtmlElement> children;
    private final SimpleHtmlElement parent;

    private String innerString = "";
    public SimpleHtmlElement(String tagName, Properties attributes, SimpleHtmlElement parent) {
        this.tagName = tagName;
        this.attributes = attributes == null ? new Properties() : attributes;
        this.parent = parent;
        children = new ArrayList<>();
    }

    @SuppressWarnings("UnusedReturnValue")
    public SimpleHtmlElement textChild(String tagName, String text) {
        SimpleHtmlElement html = new SimpleHtmlElement(tagName, this);
        html.setInnerString(text);
        appendChild(html);
        return html;
    }

    public SimpleHtmlElement child(String tagName) {
        SimpleHtmlElement html = new SimpleHtmlElement(tagName, this);
        appendChild(html);
        return html;
    }

    public SimpleHtmlElement(String tagName, SimpleHtmlElement parent) {
        this(tagName, null, parent);
    }

    public String getTagName() {
        return tagName;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public List<SimpleHtmlElement> getChildren() {
        return children;
    }

    public SimpleHtmlElement getParent() {
        return parent;
    }

    public void setInnerString(String string) {
        this.innerString = string;
    }

    public void setAttribute(String name, String value) {
        attributes.setProperty(name, value);
    }

    public SimpleHtmlElement appendChild(SimpleHtmlElement child) {
        children.add(child);
        return child;
    }

    protected SimpleHtmlElement getFirstChildByTagNameOrCreateNew(String tagName) {
        for (SimpleHtmlElement child : children) {
            if (child.getTagName().equals(tagName))
                return child;
        }

        return appendChild(new SimpleHtmlElement(tagName, this));
    }

    public void preStr(String string) {
        setInnerString("<pre>"+string+"</pre>");
    }

    @Override
    public String toString() {
        StringBuilder att = new StringBuilder();
        StringBuilder chi = new StringBuilder();
        Enumeration<?> enumeration = attributes.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = '"' + attributes.getProperty(name) + '"' + ' ';
            att.append(name).append("=").append(value);
        }

        chi.append(innerString);
        for (SimpleHtmlElement child : children) {
            chi.append(child.toString());
        }
        return "\n<" + (tagName + " " + att).trim() + ">\n" + chi + "\n</" + tagName +">";
    }
}
