package com.ezekielnewren.jettydemo;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;

public class EventSocket extends WebSocketAdapter
{
    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketBinary(byte[] b, int off, int len) {

    }

    @Override
    public void onWebSocketText(String message) throws RuntimeException
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);

        try {
            this.getRemote().sendString("right back at ya");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }
}