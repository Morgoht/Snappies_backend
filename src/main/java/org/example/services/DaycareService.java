package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Daycare;
import org.springframework.stereotype.Service;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DaycareService {
    Firestore dbFirestore = FirestoreClient.getFirestore();

    public Daycare daycareById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("daycares").document(documentId);
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
        CollectionReference collection = dbFirestore.collection("daycares");
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
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("daycares").document(daycare.getDocumentId()).set(daycare);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateDaycare(Daycare daycare) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("daycares").document(daycare.getDocumentId()).set(daycare);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteDaycare(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("daycares").document(documentId).delete();
        return "Successfully deleted daycare";
    }
}
