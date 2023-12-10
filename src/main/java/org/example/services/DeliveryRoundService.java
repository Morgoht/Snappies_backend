package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Delivery;
import org.example.models.DeliveryRound;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryRoundService {
    Firestore dbFirestore = FirestoreClient.getFirestore();

    public DeliveryRound deliveryRoundById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("deliveryRounds").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        DeliveryRound deliveryRound = new DeliveryRound();

        deliveryRound.setDocumentId(document.getId());
        deliveryRound.setName(document.getString("name"));

        // You might need to adapt this part based on your actual data model
        List<Delivery> deliveries = new ArrayList<>();
        List<DocumentReference> deliveryReferences = (List<DocumentReference>) document.get("deliveries");

        if (deliveryReferences != null) {
            for (DocumentReference deliveryReference : deliveryReferences) {
                // Fetch and add deliveries
                ApiFuture<DocumentSnapshot> deliveryFuture = deliveryReference.get();
                DocumentSnapshot deliveryDocument = deliveryFuture.get();
                Delivery delivery = deliveryDocument.toObject(Delivery.class);
                deliveries.add(delivery);
            }
        }

        deliveryRound.setDeliveries(deliveries);
        return deliveryRound;
    }

    public List<DeliveryRound> allDeliveryRounds() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("deliveryRounds");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<DeliveryRound> deliveryRoundList = new ArrayList<>();

        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            DeliveryRound deliveryRound = new DeliveryRound();
            deliveryRound.setDocumentId(doc.getId());
            deliveryRound.setName(doc.getString("name"));

            // Fetch and add deliveries
            List<Delivery> deliveries = new ArrayList<>();
            List<DocumentReference> deliveryReferences = (List<DocumentReference>) doc.get("deliveries");

            if (deliveryReferences != null) {
                for (DocumentReference deliveryReference : deliveryReferences) {
                    ApiFuture<DocumentSnapshot> deliveryFuture = deliveryReference.get();
                    DocumentSnapshot deliveryDocument = deliveryFuture.get();
                    Delivery delivery = deliveryDocument.toObject(Delivery.class);
                    deliveries.add(delivery);
                }
            }

            deliveryRound.setDeliveries(deliveries);
            deliveryRoundList.add(deliveryRound);
        }

        return deliveryRoundList;
    }

    public String createDeliveryRound(DeliveryRound deliveryRound) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore
                .collection("deliveryRounds")
                .document(deliveryRound.getDocumentId())
                .set(deliveryRound);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateDeliveryRound(DeliveryRound deliveryRound) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore
                .collection("deliveryRounds")
                .document(deliveryRound.getDocumentId())
                .set(deliveryRound);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteDeliveryRound(String documentId) {
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("deliveryRounds").document(documentId).delete();
        return "Successfully deleted delivery round";
    }
}
