package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Article;
import org.example.models.Daycare;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DaycareService {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    CollectionReference daycaresCollection = dbFirestore.collection("daycares");
    
    public Daycare daycareById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = daycaresCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Daycare daycare;
        if(document.exists()){
            daycare = document.toObject(Daycare.class);
            assert daycare != null;
            daycare.setDocumentId(document.getId());
            return daycare;
        }
        return null;
    }

    public List<Daycare> allDaycares() throws ExecutionException, InterruptedException {
        CollectionReference collection = daycaresCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();

        List<Daycare> daycareList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Daycare daycare = doc.toObject(Daycare.class);
            assert daycare != null;
            daycare.setDocumentId(doc.getId());
            daycareList.add(daycare);
        }

        return daycareList;
    }

    public String createDaycare(Daycare daycare) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = daycaresCollection.document(daycare.getDocumentId()).set(daycare);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Daycare updateDaycare( String daycareId,  String address,  String name,  String phoneNumber,  String email) throws ExecutionException, InterruptedException {
        Daycare daycareToUpdate = this.daycareById(daycareId);
        DocumentReference docRef = daycaresCollection.document(daycareId);

        if (address!= null) {
            docRef.update("address", address);
            daycareToUpdate.setAddress(address);
        }

        if (name != null) {
            docRef.update("name",name);
            daycareToUpdate.setName(name);
        }

        if (phoneNumber!= null) {
            docRef.update("phoneNumber", phoneNumber);
            daycareToUpdate.setPhoneNumber(phoneNumber);
        }
        if (phoneNumber!= null) {
            docRef.update("email", email);
            daycareToUpdate.setEmail(email);
        }

        return daycareToUpdate;
    }

    public String deleteDaycare(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = daycaresCollection.document(documentId).delete();
        return "Successfully deleted daycare";
    }
}
