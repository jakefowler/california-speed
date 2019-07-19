package com.ezekielnewren;

//import jdk.internal.jshell.tool.MessageHandler;
import jdk.internal.joptsimple.internal.Strings;
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
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Controller extends WebSocketServlet {
    private static final Logger log = Log.getLogger(Controller.class);

    static final Controller instance = new Controller();
    Map<UUID, Player> players = new HashMap<>();


    public static void go(String[] args) throws Exception {
        // example usage
        // https://commons.apache.org/proper/commons-cli/usage.html
        Options opt = new Options();
        opt.addOption("l", true, "ip address that the server will bind to");
        opt.addOption("p", true, "port number for the server to listen on");
        opt.addOption("k", true, "path to the java keystore");
        opt.addOption("i", false, "insecure, run server without tls. The server will bind to localhost unless otherwise specified if this option is used");

        // arguments that will be initialized by command line arguments
        boolean insecure;
        int port;
        InetSocketAddress localAddress;
        String jksPath;

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cl = parser.parse(opt, args);

            // this program does not support being secure and insecure simultaneously thus throw an error and exit
            if (cl.hasOption("k") && cl.hasOption("i")) {
                throw new RuntimeException("option -i and -k are mutually exclusive");
            }

            // should this program use tls?
            insecure = cl.hasOption("i");

            // port number 8080 by default
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
        assert(port >= 0);
        assert((insecure && jksPath == null) || (!insecure && jksPath != null));


        Server server = new Server();

        //String host="localhost";
        //int port = 8080;
        //String keyStorePath = "C:\\keystore";
        //String keyStorePassword = "";
        String pw;
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

        //String keyManagerPassword = "password";
        //List<Handler> webSocketHandlerList = new ArrayList();
        //MessageHandler messagehandler;


//        // connector configuration
//        SslContextFactory sslContextFactory = new SslContextFactory.Server.Server();
//        sslContextFactory.setKeyStorePath(keyStorePath);
//        sslContextFactory.setKeyStorePassword(keyStorePassword);
//        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
//        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
//        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
//        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
//        sslConnector.setHost(host);
//        sslConnector.setPort(port);
//        server.addConnector(sslConnector);
//
//        // handler configuration
//        HandlerCollection handlerCollection = new HandlerCollection();
//        handlerCollection.setHandlers(webSocketHandlerList.toArray(new Handler[0]));
//        server.setHandler(handlerCollection);
//
//        WebSocketHandler wsHandler = new WebSocketHandler() {
//            @Override
//            public void configure(WebSocketServletFactory webSocketServletFactory) {
//                webSocketServletFactory.register(Controller.class);
//            }
//        };
//        ContextHandler wsContextHandler = new ContextHandler();
//        wsContextHandler.setHandler(wsHandler);
//        wsContextHandler.setContextPath("/");  // this context path doesn't work ftm
//        webSocketHandlerList.add(wsHandler);
//
//        messagehandler = new MessageHandler();
//        new Thread(messagehandler).start();
//
//        try {
//            server.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



        ServerConnector connector = new ServerConnector(server);
        SslContextFactory sslContextFactory = new SslContextFactory.Server.Server();
        sslContextFactory.setKeyStorePath(jksPath);
        sslContextFactory.setKeyStorePassword(Strings.EMPTY);
        sslContextFactory.setKeyManagerPassword(pw);
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        //sslConnector.setHost(host);
        sslConnector.setPort(port);
        server.addConnector(sslConnector);

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

    public void updateBoard(Game game, ArrayList<Card> state) {



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



