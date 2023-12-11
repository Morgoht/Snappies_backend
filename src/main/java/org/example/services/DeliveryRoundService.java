package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Delivery;
import org.example.models.DeliveryRound;
import org.example.models.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryRoundService {
    Firestore dbFirestore = FirestoreClient.getFirestore();


    public DeliveryRound setDeliveryRoundFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            DeliveryRound deliveryRound = new DeliveryRound();
            deliveryRound.setDocumentId(doc.getId());

            //Instanciate required Services
            DeliveryService deliveryService = new DeliveryService();
            //Find and fill the Deliveries table
            for (String id: UtilService.listOfReferences(doc,"deliveries")
            ) {
                deliveryRound.addDelivery(deliveryService.deliveryById(id));
            }

            deliveryRound.setRoundEnded((Boolean) doc.get("roundEnded"));
            deliveryRound.setName((String) doc.get("name"));

            return deliveryRound;
        } else {
            return null;
        }
    }


    public DeliveryRound deliveryRoundById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("deliveriesRounds").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setDeliveryRoundFromDocumentSnapshot(document);
    }

    public List<DeliveryRound> allDeliveryRounds() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("deliveriesRounds");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<DeliveryRound> deliveryRoundList = new ArrayList<>();

        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            deliveryRoundList.add(setDeliveryRoundFromDocumentSnapshot(doc));
        }

        return deliveryRoundList;
    }

    public String createDeliveryRound(DeliveryRound deliveryRound) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore
                .collection("deliveriesRounds")
                .document(deliveryRound.getDocumentId())
                .set(deliveryRound);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateDeliveryRound(DeliveryRound deliveryRound) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore
                .collection("deliveriesRounds")
                .document(deliveryRound.getDocumentId())
                .set(deliveryRound);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String deleteDeliveryRound(String documentId) {
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("deliveriesRounds").document(documentId).delete();
        return "Successfully deleted delivery round";
    }

    public Delivery addDelivery(String documentId, Delivery delivery) throws ExecutionException, InterruptedException {
        DocumentReference deliveryRoundRef = dbFirestore.collection("deliveriesRounds").document(documentId);

        // Fetch existing deliveries
        ApiFuture<DocumentSnapshot> deliveryRoundFuture = deliveryRoundRef.get();
        DocumentSnapshot deliveryRoundDocument = deliveryRoundFuture.get();
        List<DocumentReference> existingDeliveries = (List<DocumentReference>) deliveryRoundDocument.get("deliveries");

        if (existingDeliveries == null) {
            existingDeliveries = new ArrayList<>();
        }

        // Add the new delivery to the list of deliveries
        ApiFuture<DocumentReference> newDeliveryFuture = dbFirestore.collection("deliveries").add(delivery);
        DocumentReference newDeliveryRef = newDeliveryFuture.get();
        existingDeliveries.add(newDeliveryRef);

        // Update the 'deliveries' field in the delivery round document
        deliveryRoundRef.update("deliveries", existingDeliveries);

        return delivery;
    }

}
