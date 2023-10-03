package com.panjohnny.pjge.app;

import com.panjohnny.quickhttp.jhtml.JHTMLElement;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.util.Objects;

@SuppressWarnings("unused")
public class FileMenu extends JHTMLElement {

    @Override
    public void createElement(Element element) {
        element.tagName("div");
        element.addClass("menu");
        Attributes at = element.attributes();
        element.id(at.get("name"));

        Element b = Objects.requireNonNull(element.ownerDocument()).createElement("b");
        b.text((at.get("name").charAt(0) + "").toUpperCase() + at.get("name").substring(1));

        Element ul = Objects.requireNonNull(element.ownerDocument()).createElement("ul");
        ul.attr("hidden", true);

        for (String s : at.get("items").split(",")) {
            Element li = Objects.requireNonNull(element.ownerDocument()).createElement("li");
            li.html("<a href=\"javascript:executeItem('%s')\">%s</a>".formatted( s.trim().toLowerCase(), s.trim()));

            ul.appendChild(li);
        }

        at.remove("items");

        element.appendChild(b);
        element.appendChild(ul);
    }
}
