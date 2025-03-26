package ch.uzh.ifi.hase.soprafs24.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseService {

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Load the JSON key from resources
            InputStream credentials = new FileInputStream("/Users/melihserin/Desktop/SoPra/sopra-fs25-group40-server-firebase-baa96612c030.json");

            System.out.println("✅ Firebase Service Account is set.");
            System.out.println("🔍 Checking GCP_SERVICE_CREDENTIALS: " + credentials.toString());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentials))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // Prevent re-initialization
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }

    public void sendInvitationNotification(Long senderId, Long receiverId, Long quizId, String fcmToken) {
        try {
            // Create Notification
            Notification notification = Notification.builder()
                    .setTitle("Quiz Invitation")
                    .setBody("You have been invited to a quiz!")
                    .build();

            // Add Custom Data (Quiz ID, Sender ID)
            Map<String, String> data = new HashMap<>();
            data.put("type", "invitation");
            data.put("quizId", quizId.toString());
            data.put("fromUserId", senderId.toString());
            data.put("toUserId", receiverId.toString());

            // Create Message
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            // Send the message via Firebase
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendQuizResponseNotification(User sender, Long quizId, Boolean accepted) {
        try {
            String title = accepted ? "Quiz Started!" : "Invitation Declined";
            String body = accepted ? "Your quiz invitation was accepted!" : "Your quiz invitation was declined.";
            
            // Create Notification
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Add Custom Data (Quiz ID, Sender ID)
            Map<String, String> data = new HashMap<>();
            data.put("type", "response");
            data.put("quizId", quizId.toString());
            data.put("accepted", String.valueOf(accepted));

            // Create Message
            Message message = Message.builder()
                    .setToken(sender.getFcmToken())
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            // Send the message via Firebase
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

