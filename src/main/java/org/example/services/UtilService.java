package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import org.example.models.OrderLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UtilService {


    public static String findByReference(DocumentSnapshot doc, String fieldName) throws ExecutionException, InterruptedException {
        DocumentReference ref = (DocumentReference) doc.get(fieldName);
        assert ref != null;
        ApiFuture<DocumentSnapshot> future = ref.get();
        DocumentSnapshot document = future.get();
        return document.getId();
    }

    public static List<String> listOfReferences(DocumentSnapshot doc, String collection) throws ExecutionException, InterruptedException {
        List<DocumentReference> refList = new ArrayList<>();
        if(doc.get(collection)!=null) {
            refList = (List<DocumentReference>) doc.get(collection);
        }
        List<String> finalList = new ArrayList<>();
        for (DocumentReference ref: refList
        ) {
            ApiFuture<DocumentSnapshot> future = ref.get();
            DocumentSnapshot document = future.get();
            finalList.add(document.getId());
        }
        return finalList;

    }

}
