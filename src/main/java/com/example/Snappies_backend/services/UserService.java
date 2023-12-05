package com.example.Snappies_backend.services;

import com.example.Snappies_backend.models.Article;
import com.example.Snappies_backend.models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void createUser(User user){
        Firestore firestore = FirestoreClient.getFirestore();

        DocumentReference docReference = firestore.collection("users").document();
        ApiFuture<WriteResult> apiFuture = docReference.set(user);
    }
}
