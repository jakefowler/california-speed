package com.ezekielnewren;

import org.apache.commons.cli.*;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.util.*;

public class Controller extends WebSocketServlet {
    private static final Logger log = Log.getLogger(Controller.class);

    static final Controller instance = new Controller();
    Map<UUID, Player> players = new HashMap<>();
    Map<UUID, Game> game = new HashMap<>();

    public static void go(String[] args) throws Exception {
        // example usage
        // https://commons.apache.org/proper/commons-cli/usage.html
        Options opt = new Options();
        opt.addOption("l", true, "ip address that the server will bind to");
        opt.addOption("p", true, "port number for the server to listen on");
        opt.addOption("k", true, "path to a java keystore containing a keypair and a certificate chain");
        opt.addOption("i", false, "insecure, run server without tls. An exception will be thrown if a non loopback/localhost address is supplied");

        // arguments that will be initialized by command line arguments
        boolean insecure;
        InetSocketAddress localAddress;
        String jksPath;

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cl = parser.parse(opt, args);

            // this program does not support being secure and insecure simultaneously thus throw an error and exit
            if (cl.hasOption("k") && cl.hasOption("i")) {
                throw new RuntimeException("option -i and -k are mutually exclusive");
            }

            if (!cl.hasOption("k") && !cl.hasOption("i")) {
                throw new RuntimeException("the server must be run with a path to a java keystore via -k or be explicitly told to run insecurely -i");
            }

            // should this program use tls?
            insecure = cl.hasOption("i");

            // port number 8080 by default
            int port;
            if (cl.hasOption("p")) {
                String raw = cl.getOptionValue("p");
                try {
                    port = Integer.parseInt(raw);
                    if (!(0 <= port && port < 65536)) {
                        throw new RuntimeException("'"+raw+"' is not a valid port number");
                    }
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("'"+raw+"' not an integer let alone a valid port number");
                }
            } else {
                port = 8080;
            }

            // if running with -i (insecure) then refuse to bind to a non loopback address
            if (cl.hasOption("l")) {
                InetSocketAddress tmp = new InetSocketAddress(cl.getOptionValue("l"), port);
                if (insecure && !tmp.getAddress().isLoopbackAddress()) {
                    //tmp = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
                    throw new IOException("cannot bind to a non loopback address when running with -i (insecure)");
                }
                localAddress = tmp;
            } else {
                if (!insecure) {
                    localAddress = new InetSocketAddress((InetAddress)null, port);
                } else {
                    localAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
                }
            }

            // path of the java keystore to use
            if (cl.hasOption("k")) {
                jksPath = cl.getOptionValue("k");
                if (!Files.exists(Paths.get(jksPath))) {
                    new HelpFormatter().printHelp("speed", opt);
                }
            } else {
                jksPath = null;
            }
        } catch (ParseException e) {
            new HelpFormatter().printHelp("speed", opt);
            return;
        }

        // sanity check
        assert(localAddress != null);
        assert((insecure && jksPath == null) || (!insecure && jksPath != null));


        Server server = new Server();

        if (!insecure) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(jksPath), new char[0]);
            String alias = ks.aliases().nextElement();

            boolean blankPassword;
            try {
                Key key = ks.getKey(alias, new char[0]);
                blankPassword = true;
            } catch (UnrecoverableKeyException e) {
                blankPassword = false;
            }



            String pw;
            if (blankPassword) {
                pw = "";
            } else {
                Console c = System.console();
                String prompt = "keypair password: ";
                System.out.print(prompt);
                if (c != null) {
                    pw = new String(c.readPassword());
                } else {
                    log.warn("cannot use console, password will show up when typed in");
                    Scanner stdin = new Scanner(System.in);
                    pw = stdin.nextLine();
                }
            }

            ServerConnector connector = new ServerConnector(server);
            SslContextFactory sslContextFactory = new SslContextFactory.Server.Server();
            sslContextFactory.setKeyStorePath(jksPath);
            sslContextFactory.setKeyStorePassword("");
            sslContextFactory.setKeyManagerPassword(pw);
            SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
            HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
            ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
            sslConnector.setHost(localAddress.getAddress().getHostAddress());
            sslConnector.setPort(localAddress.getPort());
            server.addConnector(sslConnector);

            log.info(localAddress.toString()+" secure websocket setup");
        } else {
            ServerConnector connector = new ServerConnector(server);
            connector.setHost(localAddress.getAddress().getHostAddress());
            connector.setPort(localAddress.getPort());
            server.addConnector(connector);

            log.info(localAddress.toString()+" running without tls");
        }

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", Controller.class);
        context.addServlet(holderEvents, "/");

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

    public void updateBoard(Game game, ArrayList<Card> state, boolean isDraw) {
        JSONObject json = new JSONObject();
        JSONObject push = json.put("push", new JSONObject()).getJSONObject("push");
        JSONObject board = push.put("board", new JSONObject()).getJSONObject("board");
        JSONArray pile = board.put("pile", new JSONArray()).getJSONArray("pile");

        for (Card card: state) {
            ArrayList<Card> al = new ArrayList();
            al.add(card);
            pile.put(toJsonDeck(al));
        }

        game.sendBoth(json);
    }

    public static Controller getInstance() {
        return instance;
    }

    public void register(Player player) {
        players.put(player.getUUID(), player);
    }

    void matchPlayers() {
        log.info("matching players");

        ArrayList<Player> inLobby = new ArrayList<>(players.values());
        inLobby.removeIf((p)-> p.inGame());

        while (inLobby.size() >= 2) {
            Player p0 = inLobby.remove(0);
            Player p1 = inLobby.remove(0);

            // create game object
            Game g = new Game(p0, p1);
            game.put(p0.getUUID(), g);
            game.put(p1.getUUID(), g);

            // format json
            JSONObject json = new JSONObject();
            JSONObject push = json.put("push", new JSONObject()).getJSONObject("push");

            push.put("gameStart", true);
            JSONArray arr = push.put("players", new JSONArray()).getJSONArray("players");
            for (Player p: players.values()) {
                arr.put(toJsonPlayer(p, false));
            }

            // let the players know that the game is starting
            g.sendBoth(json);
            g.updateGameboard();
        }
    }

    public void unregister(Player player) {
        player.ready = false;
        players.remove(player.id);
        game.remove(player.id);
    }


    public JSONObject toJsonPlayer(Player p, boolean header) {
        JSONObject json = new JSONObject();
        JSONObject sub = json;
        if (header) {
            sub = sub.put("player", new JSONObject()).getJSONObject("player");
        }
        sub.put("id", p.id.toString());
        sub.put("name", p.getName());
        return json;
    }

    public Player fromJsonPlayer(JSONObject json) {
        JSONObject player = json.getJSONObject("player");
        UUID id = UUID.fromString(player.getString("id"));
        Player pobj = players.get(id);
        return pobj;
    }

    public JSONObject toJsonCard(Card c) {
        JSONObject json = new JSONObject();
        //JSONArray deck = json.put("deck", new JSONArray()).getJSONArray("deck");

        //JSONObject card = new JSONObject();
        json.put("suit", c.suit.toString().toLowerCase().substring(0, 1));
        json.put("rank", c.rank);

        //deck.put(card);
        return json;
    }

    public JSONObject toJsonDeck(Deck d) {
        return toJsonDeck(d.getDeck());
    }

    public JSONObject toJsonDeck(ArrayList<Card> alc) {
        JSONObject json = new JSONObject();
        JSONArray deck = json.put("deck", new JSONArray()).getJSONArray("deck");

        for (Card c: alc) {
            deck.put(toJsonCard(c));
        }

        return json;
    }

    public void gameOver(Game g, Player _winner) {
        Card prevPlayedCard = _winner.prevPlayedCard;

        JSONObject json = new JSONObject();

        JSONObject push = json.put("push", new JSONObject()).getJSONObject("push");

        push.put("gameOver", true);
        //JSONObject winner = push.put("winner", new JSONObject()).getJSONObject("winner");
        JSONObject winner = push.put("winner", toJsonPlayer(_winner, false)).getJSONObject("winner");
        winner.put("prevPlayedCard", toJsonCard(prevPlayedCard));

        g.sendBoth(json);
    }

//    public Card fromJsonCard(JSONObject json, boolean header) {
//        JSONObject sub = json;
//        if (header) {
//            sub = sub.getJSONObject("card");
//        }
//        return new Card(sub.getString("suit"), sub.getInt("rank"));
//    }

}



