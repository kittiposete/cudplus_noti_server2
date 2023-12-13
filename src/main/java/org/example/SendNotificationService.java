package org.example;

import java.util.ArrayList;

public class SendNotificationService {
    void run(DatabaseConnection databaseConnection) {
        while (true) {
            ArrayList<SubscriptionData> subscriptionData = databaseConnection.getSubscriptionData();
            for (SubscriptionData data : subscriptionData) {
                System.out.println("Checking for " + data.getUsername());
                String username = data.getUsername();
                String password = data.getPassword();
                BotAdapter botAdapter = new BotAdapter();

                String oldChatData = databaseConnection.readChatData(username);
                GetChatDataResult newChatData = botAdapter.getChatData(username, password);
                System.out.println("finished getting chat data");
                if (newChatData.getStatus() != BotResult.SUCCESS) {
                    System.out.println("Error: " + newChatData.getStatus());
                    continue;
                }
                String newChatDataString = newChatData.getChatData();
                assert oldChatData != null;
                if (!oldChatData.equals(newChatDataString)) {
                    String deviceId = data.getDeviceId();
                    String title = "New Chat";
                    String message = "new chat";
                    try {
                        new PushNotification().pushNotification(title, message, deviceId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No new chat");
                }
            }
        }
    }
}
