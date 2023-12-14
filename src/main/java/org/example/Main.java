package org.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.example.controllers.DeliveryRoundController;
import org.example.models.Delivery;
import org.example.models.DeliveryRound;
import org.example.services.DeliveryRoundService;
import org.example.services.DeliveryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:4200")
public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        ClassLoader classLoader = Main.class.getClassLoader();

        File file = new File(Objects.requireNonNull(classLoader.getResource("service-account-key.json")).getFile());

        FileInputStream serviceAccount =
                new FileInputStream(file.getAbsolutePath());
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://snappiespfe-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        FirebaseApp.initializeApp(options);
        SpringApplication.run(Main.class,args);


    }
}