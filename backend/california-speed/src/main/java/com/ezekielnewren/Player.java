package com.ezekielnewren;

import jdk.internal.joptsimple.internal.Strings;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import java.util.ArrayList;

public class Player extends WebSocketAdapter implements Closeable {
    private static final Logger log = Log.getLogger(Controller.class);

    Controller ctrl;

    UUID id;
    String name;
    boolean ready;
    Deck mainDeck;
    ArrayList<Card> coveredCards;


    public Player() {
        ctrl = Controller.getInstance();
        name = Strings.EMPTY;
    }

    void init(UUID _id, String _name) {
        id = _id;

        ctrl.register(this);
    }

    void init() {
        init(UUID.randomUUID(), "");
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        log.info("Socket Connected: " + sess);

        init();

        //send(new JSONObject().put("push", ctrl.toJsonPlayer(this)));
    }

    @Override
    public void onWebSocketBinary(byte[] b, int off, int len) {
        super.onWebSocketBinary(b, off, len);
    }

    @Override
    public void onWebSocketText(String message) throws RuntimeException {
        super.onWebSocketText(message);
        log.info("Received TEXT message: " + message);
        JSONObject input = new JSONObject(message);


        if (!input.isNull("request")) {
            JSONObject req = input.getJSONObject("request");
            if (!req.isNull("msg")) {
                JSONObject tmp = new JSONObject();

                JSONObject push = tmp.put("push", new JSONObject()).getJSONObject("push");

                String msg = input.getJSONObject("request").getString("msg");

                push.put("chat", ctrl.toJsonPlayer(this, true));
                push.getJSONObject("chat").put("msg", msg);

                ctrl.sendAll(tmp);

                log.info("");
            } else if (req.length() == 1 && !req.isNull("player")) {
                setName(req.getJSONObject("player").getString("name"));
                send(new JSONObject().put("response", ctrl.toJsonPlayer(this, true)));
                ctrl.matchPlayers();
            } else {
                log.warn("unknown request: "+input.getJSONObject("request").toString());
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
        close();
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        log.warn(cause);
        //cause.printStackTrace(System.err);
        close();
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
        if (id == null) throw new NullPointerException();
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String _name) {
        name = _name;
        ready = true;
    }

    boolean isReady() {
        return ready;
    }

    boolean inGame() {
        return ctrl.game.containsKey(id);
    }

    public void sendChat(String msg) {
        JSONObject raw = new JSONObject();
        raw.put("push", ctrl.toJsonPlayer(this, true));
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
