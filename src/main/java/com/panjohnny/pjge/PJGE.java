package com.panjohnny.pjge;

import com.panjohnny.pjge.ws.CommandProcessor;
import com.panjohnny.pjge.ws.Communicator;
import com.panjohnny.pjgl.api.PJGLEvents;
import com.panjohnny.quickhttp.Router;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

@SuppressWarnings("unused")
public class PJGE {
    public static final String VERSION = "0.1-alpha";
    public static final System.Logger LOGGER = System.getLogger("PJGE");

    private static String mode;
    public static void startDebugger() {
        mode = "debugger";
        CommandProcessor.registerDebuggerCommands();
        runServerAndWebSocket();
    }

    private static void runServerAndWebSocket() {
        Thread pjgeMain = new Thread(() -> {
            HttpServer server;
            try {
                server = HttpServer.create(new InetSocketAddress(1234), 0);
                HttpServer finalServer = server;
                PJGLEvents.EXIT.listen(() -> {
                    LOGGER.log(System.Logger.Level.INFO, "Closing server...");
                    finalServer.stop(0);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Communicator com = new Communicator(new InetSocketAddress(1235));
            PJGLEvents.EXIT.listen(() -> {
                try {
                    LOGGER.log(System.Logger.Level.INFO, "Closing WS...");
                    com.stop();
                } catch (InterruptedException ignored) {
                }
            });

            Router router = new Router(server);

            router.loadFromAnnotations(new WebInterface());

            Thread th = new Thread(com::start, "pjge-ws");
            th.start();
            server.start();
        }, "pjge-main");

        pjgeMain.start();
    }

    public static void main(String[] args) {
        mode = "creator";
        CommandProcessor.registerCreatorCommands();
        runServerAndWebSocket();
    }

    public static String getMode() {
        return mode;
    }
}
