package org.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WriteToFirebase {

    public static void write(String host, int port) {
        try {
            InputStream serviceAccount = WriteToFirebase.class.getClassLoader().getResourceAsStream("summerpractick-firebase-adminsdk-9m1sj-e62342d3f5.json");
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccount);
            GoogleCredentials scoped = credentials.createScoped(
                    Arrays.asList(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"
                    )
            );
            String jsonInputString = "{ \"ip\": \"%s\", \"port\": %d}";
            jsonInputString = String.format(jsonInputString, host, port);
            scoped.refreshIfExpired();
            String token = scoped.getAccessToken().getTokenValue();

            String firebaseProjectUrl = "https://summerpractick-default-rtdb.europe-west1.firebasedatabase.app/chess.json";

            URL url = new URL(firebaseProjectUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully updated in Firebase.");
            } else {
                System.out.println("Failed to update data. Response Code: " + responseCode);
            }
           System.out.println("Data written to Firebase successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
