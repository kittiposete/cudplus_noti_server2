package org.example;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class myWebSocket {

    @OnWebSocketConnect
    public void connected(Session session){

    }

    @OnWebSocketMessage
    public void message(Session session, String message){

    }
}
