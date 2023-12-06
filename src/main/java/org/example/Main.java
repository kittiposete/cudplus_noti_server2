package org.example;

public class Main {
    public static void main(String[] args) {
        WebSocketHandler clientHandler = new WebSocketHandler(3881);
        clientHandler.start();
        System.out.println("Web socket server started on port " + 3881);
    }
}