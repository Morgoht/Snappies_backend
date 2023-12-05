package com.example.Snappies_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FirebaseConfig {

    public void configureFirebaseConnection() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("classpath:config/snappiespfe-firebase-adminsdk-fkcbn-8b8554b409.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://snappiespfe-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);

    }
}
