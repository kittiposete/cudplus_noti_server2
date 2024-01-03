package org.example;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import java.io.FileInputStream;
import java.io.IOException;

public class PushNotification {
    public PushNotification() {
        loadFirebaseConfig();
    }

    private void loadFirebaseConfig() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/cudplus-noti2-firebase-adminsdk-89971-b838876e9f.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://cudplus-noti2.firebaseio.com/")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.out.println("Failed to initialize Firebase: " + e.getMessage());
        }
    }

    public void pushNotification(String title, String message, String deviceId) {
        // Create a message
        Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title) // Set the title of the notification
                        .setBody(message) // Set the body of the notification
                        .build())
                .setToken(deviceId) // Set the device token
                .build();

        // Send the message
        try {
            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.out.println("Failed to send message: " + e.getMessage());
        }
    }
}