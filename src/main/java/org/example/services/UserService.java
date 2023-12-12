package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Delivery;
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
    CollectionReference usersCollection =dbFirestore.collection("users");

    public User userById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = usersCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if(document.exists()){
            user = document.toObject(User.class);
            user.setDocumentId(document.getId());
            return user;
        }
        return null;
    }

    public List<User> allUsers() throws ExecutionException, InterruptedException {
        CollectionReference collection = usersCollection;
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
        ApiFuture<WriteResult> collectionsApiFuture = usersCollection.document(user.getDocumentId()).set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public User updateUser(String userId,String name, String lastname,String username,String email,String password,String phoneNumber) throws ExecutionException, InterruptedException {
        User userToUpdate = this.userById(userId);
       DocumentReference docRef = usersCollection.document(userId);

        if (name!= null) {
            docRef.update("name", name);
            userToUpdate.setName(name);
        }

        if (lastname != null) {
            docRef.update("lastName",lastname);
            userToUpdate.setLastname(lastname);
        }

        if (username!= null) {
            docRef.update("username", username);
            userToUpdate.setUsername(username);
        }

        if (email != null) {
            docRef.update("email", email);
            userToUpdate.setEmail(email);
        }

        if (password != null) {
            docRef.update("password", password);
            userToUpdate.setPassword(password);
        }

        if (phoneNumber != null) {
            docRef.update("phoneNumber", phoneNumber);
            userToUpdate.setPhoneNumber(phoneNumber);
        }
        return userToUpdate;
    }

    public String deleteUser(String documentId){
        usersCollection.document(documentId).delete();
        return "Successfully deleted user";
    }
}
