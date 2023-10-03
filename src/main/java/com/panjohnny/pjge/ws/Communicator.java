package com.panjohnny.pjge.ws;

import com.panjohnny.Logger;
import com.panjohnny.pjge.PJGE;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.PrintStream;
import java.net.InetSocketAddress;

public class Communicator extends WebSocketServer {
    public Communicator(InetSocketAddress address) {
        super(address);
        System.setOut(new PrintStream(System.out) {
            private void hook(String s) {
                broadcast("CONSOLE.APPEND " + s);
            }

            @Override
            public void print(String s) {
                super.print(s);
                hook(s);
            }
        });

        try {
            ((Logger) System.getLogger("PJGL")).setStream(System.out);
            ((Logger) System.getLogger("PJGE")).setStream(System.out);
        } catch (Exception ignored) {
            PJGE.LOGGER.log(System.Logger.Level.WARNING, "There was an error updating outputs for loggers. Eh, no-one cares.");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        PJGE.LOGGER.log(System.Logger.Level.INFO, "Client connected {0}", clientHandshake);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        PJGE.LOGGER.log(System.Logger.Level.INFO, "WebSocket closed {0} {1} {2}", i, s, b);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        webSocket.send(CommandProcessor.process(s));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
