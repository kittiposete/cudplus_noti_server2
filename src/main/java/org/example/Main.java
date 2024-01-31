package org.example;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        new Thread(() -> {
            WebSocketHandler clientHandler = new WebSocketHandler(3881, databaseConnection);
            clientHandler.start();
        }).start();

        // run sendNotification service in new thread
        new Thread(() -> {
            new SendNotificationService().run(databaseConnection);
        }).start();
    }
}