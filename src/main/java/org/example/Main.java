package org.example;

public class Main {
    public static void main(String[] args) {
        WebSocketHandler clientHandler = new WebSocketHandler(3881);
        clientHandler.start();

    }
}