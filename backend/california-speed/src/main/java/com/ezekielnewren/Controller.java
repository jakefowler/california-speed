package com.ezekielnewren;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.json.JSONObject;

import java.util.*;

public class Controller extends WebSocketServlet {
    private static final Logger log = Log.getLogger(Controller.class);

    static final Controller instance = new Controller();
    Map<UUID, Player> players = new HashMap<>();


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
        ServletHolder holderEvents = new ServletHolder("ws-events", Controller.class);
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
    public void configure(WebSocketServletFactory factory) {
        factory.register(Player.class);
    }

    public void sendAll(JSONObject json){
        for (UUID id: players.keySet()) {
            Player p = players.get(id);
            p.send(json);
        }
    }

    public void updateBoard(Game game, Card[] state) {



    }

    public static Controller getInstance() {
        return instance;
    }

    public void register(Player player) {
        players.put(player.getUUID(), player);
        player.valid = true;
    }

    public void unregister(Player player) {
        player.valid = false;
        players.remove(player.id);
    }


    public JSONObject toJsonPlayer(Player p) {
        JSONObject json = new JSONObject();
        json.put("player", new JSONObject());
        JSONObject player = json.getJSONObject("player");
        player.put("id", p.id.toString());
        player.put("name", p.name);
        return json;
    }

    public Player fromJsonPlayer(JSONObject json) {
        JSONObject player = json.getJSONObject("player");
        UUID id = UUID.fromString(player.getString("id"));
        Player pobj = players.get(id);
        return pobj;
    }

}



