package com.ezekielnewren;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class Controller extends WebSocketAdapter {
    private static final Logger log = Log.getLogger(Controller.class);
    UUID id;

    public static void go(String[] args) {

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", ControllerServlet.class);
        context.addServlet(holderEvents, "/events/*");

        try {
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        log.info("Socket Connected: " + sess);
        id = UUID.randomUUID();
        send(id.toString());
    }

    @Override
    public void onWebSocketBinary(byte[] b, int off, int len) {
        super.onWebSocketBinary(b, off, len);
    }

    @Override
    public void onWebSocketText(String message) throws RuntimeException {
        super.onWebSocketText(message);
        log.info("Received TEXT message: " + message);

        send("right back at ya");
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode,reason);
        log.info("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        log.warn(cause);
        //cause.printStackTrace(System.err);
    }

    public void send(String msg) throws RuntimeException {
        try {
            getRemote().sendString(msg);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void send(JSONObject json) {
        String tmp = json.toString();
        if (tmp == null) throw new NullPointerException("is the json formatted correctly?");
        send(tmp);
    }

}
