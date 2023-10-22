module com.panjohnny.pjge {
    exports com.panjohnny.pjge;
    exports com.panjohnny.pjge.ws;
    exports com.panjohnny.pjsl.lexer;
    exports com.panjohnny.pjsl.compiler;
    requires java.desktop;
    requires jdk.httpserver;
    requires org.jsoup;
    requires org.java_websocket;
    requires com.panjohnny.pjgl;
    requires Logger;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires net.bytebuddy;
    requires JColor;
}