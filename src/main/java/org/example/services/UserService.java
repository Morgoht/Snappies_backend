package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {


    Firestore dbFirestore = FirestoreClient.getFirestore();

    public User userById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("users").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if(document.exists()){
            user = document.toObject(User.class);
            return user;
        }
        return null;
    }

    public List<User> allUsers() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("users");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();

        List<User> userList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            User user = doc.toObject(User.class);
            assert user != null;
            user.setDocumentId(doc.getId());
            userList.add(user);
        }

        return userList;
    }

    public String createUser(User user) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getDocumentId()).set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateUser(User user) throws ExecutionException, InterruptedException {
        Map<String, Object> updates = new HashMap<>();

        if (user.getName() != null) {
            updates.put("name", user.getName());
        }

        if (user.getLastname() != null) {
            updates.put("lastname", user.getLastname());
        }

        if (user.getUsername() != null) {
            updates.put("username", user.getUsername());
        }

        if (user.getEmail() != null) {
            updates.put("email", user.getEmail());
        }

        if (user.getPassword() != null) {
            updates.put("password", user.getPassword());
        }

        if (user.getPhoneNumber() != null) {
            updates.put("phoneNumber", user.getPhoneNumber());
        }
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore
                .collection("users")
                .document(user.getDocumentId())
                .update(updates);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteUser(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("users").document(documentId).delete();
        return "Successfully deleted user";
    }
}
