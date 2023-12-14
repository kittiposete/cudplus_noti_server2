package org.example;

import java.util.ArrayList;

public class SendNotificationService {
    private void logging(String message) {
        System.out.println("(Send Notification Service): " + message);
    }
    void run(DatabaseConnection databaseConnection) {
        while (true) {
            ArrayList<SubscriptionData> subscriptionData = databaseConnection.getSubscriptionData();
            for (SubscriptionData data : subscriptionData) {
                logging("Checking for " + data.getUsername());
                String username = data.getUsername();
                String password = data.getPassword();
                BotAdapter botAdapter = new BotAdapter();

                GetChatDataResult newChatData = botAdapter.getChatData(username, password);
                logging("(Bot Service): finished getting chat data");
                String oldChatData = databaseConnection.readChatData(username);
                logging("finished reading chat data");
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
