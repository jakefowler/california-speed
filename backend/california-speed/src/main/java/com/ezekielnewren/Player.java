package com.ezekielnewren;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class Player extends WebSocketAdapter implements Closeable {
    private static final Logger log = Log.getLogger(Controller.class);

    Controller ctrl;

    UUID id;
    String name;
    boolean valid;

    public Player() {
        ctrl = Controller.getInstance();
        ctrl.register(this);
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        log.info("Socket Connected: " + sess);
        id = UUID.randomUUID();
        name = "";

        send(new JSONObject().put("push", ctrl.toJsonPlayer(this)));
    }

    @Override
    public void onWebSocketBinary(byte[] b, int off, int len) {
        super.onWebSocketBinary(b, off, len);
    }

    @Override
    public void onWebSocketText(String message) throws RuntimeException {
        super.onWebSocketText(message);
        log.info("Received TEXT message: " + message);
        JSONObject raw = new JSONObject(message);


        if (!raw.isNull("request")) {
            if (!raw.isNull("request.msg")) {
                JSONObject tmp = new JSONObject();
                tmp.put("push.chat", ctrl.toJsonPlayer(this));
                tmp.put("push.chat.msg", raw.getString("request.msg"));


                log.info("");
            } else {
                log.warn("unknown request: "+raw.getJSONObject("request").toString());
            }
        } else {
            log.warn("client sent something other than a request");
        }


        //send("right back at ya");
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

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Player)) return false;
        Player that = (Player) other;
        return this.id.equals(that.id);
    }

    public UUID getUUID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void sendChat(String msg) {
        JSONObject raw = new JSONObject();
        raw.put("push", ctrl.toJsonPlayer(this));
        raw.put("msg", msg);
        send(raw);
    }

    public void send(JSONObject json) {
        String tmp = json.toString();
        if (tmp == null) throw new NullPointerException("is the json formatted correctly?");
        try {
            getRemote().sendString(tmp);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void close() {
        ctrl.unregister(this);
        super.getSession().close();
    }
}
