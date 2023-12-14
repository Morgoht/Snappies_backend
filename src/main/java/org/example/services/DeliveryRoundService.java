package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
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

     CollectionReference deliveryRoundsCollection = dbFirestore.collection("deliveryRounds");



    public List<DeliveryRound> allDeliveryRoundsBatched() throws ExecutionException, InterruptedException {
        CollectionReference collection = deliveryRoundsCollection;
        int batchSize = 50; // Set your preferred batch size
        List<DeliveryRound> deliveryRoundList = new ArrayList<>();
        // Start query with initial batch
        Query query = collection.limit(batchSize);
        while (true) {
            // Fetch the documents in batches
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                DeliveryRound deliveryRound = setDeliveryRoundFromDocumentSnapshot(doc);
                if (deliveryRound != null) {
                    deliveryRoundList.add(deliveryRound);
                }
            }
            // If there are fewer documents than the batch size, no more documents to fetch
            if (querySnapshot.get().size() < batchSize) {
                break;
            }
            // Fetch the next batch of documents after the last one in the current batch
            DocumentSnapshot lastDocument = querySnapshot.get().getDocuments().get(querySnapshot.get().size() - 1);
            query = collection.startAfter(lastDocument).limit(batchSize);
        }

        return deliveryRoundList;
    }



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
            if(doc.get("driver")!=null) {
                deliveryRound.setDriver(new UserService().userById(UtilService.findByReference(doc, "driver")));
            }
            deliveryRound.setName((String) doc.get("name"));

            return deliveryRound;
        } else {
            return null;
        }
    }


    public DeliveryRound deliveryRoundById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = deliveryRoundsCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setDeliveryRoundFromDocumentSnapshot(document);
    }



    public List<DeliveryRound> allDeliveryRounds() throws ExecutionException, InterruptedException {
        CollectionReference collection = deliveryRoundsCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<DeliveryRound> deliveryRoundList = new ArrayList<>();

        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            deliveryRoundList.add(setDeliveryRoundFromDocumentSnapshot(doc));
        }

        return deliveryRoundList;
    }


    public String createDeliveryRound(DeliveryRound deliveryRound, String driverId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = deliveryRoundsCollection.document(deliveryRound.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(deliveryRound);
        docRef.update("name", deliveryRound.getName());
        if(driverId!=null) {
            DocumentReference userReference = dbFirestore.collection("users").document(driverId);
            docRef.update("driver", userReference);
        }
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public DeliveryRound updateDeliveryRound(String deliveryRoundId, String name, String driverId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = deliveryRoundsCollection.document(deliveryRoundId);
        DeliveryRound deliveryRound = this.deliveryRoundById(deliveryRoundId);
        deliveryRound.setName(name);
        deliveryRound.setDriver(new UserService().userById(driverId));
        DocumentReference userReference =  dbFirestore.collection("users").document(driverId);
        if(name!=null) {
            docRef.update("name", name);
        }
        if(driverId!=null) {
            docRef.update("driver", userReference);
        }
        return deliveryRound;
    }

    public String deleteDeliveryRound(String documentId) {
        deliveryRoundsCollection.document(documentId).delete();
        return "Successfully deleted delivery round";
    }

    public String endRound(String documentId) throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = this.deliveryRoundById(documentId);
        for (Delivery d: deliveryRound.getDeliveries()
             ) {
            if(!d.isDelivered()) return "Not all deliveries are closed";
        }
        deliveryRound.setRoundEnded(true);
        deliveryRoundsCollection
                .document(documentId)
                .update("roundEnded", true);
        return "Successfully ended the round";
    }

    public String restartRound(String documentId) throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = this.deliveryRoundById(documentId);
        deliveryRound.setRoundEnded(false);
        deliveryRoundsCollection
                .document(documentId)
                .update("roundEnded", false);
        for (Delivery d: deliveryRound.getDeliveries()
             ) {
            new DeliveryService().resetDelivery(d.getDocumentId());
        }
        return "Reset the round status";
    }

    public boolean addDelivery(String documentId, String deliveryId) throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = this.deliveryRoundById(documentId);
        DocumentReference deliveryRef = dbFirestore.collection("deliveries").document(deliveryId);
        deliveryRoundsCollection
                .document(documentId)
                .update("deliveries", FieldValue.arrayUnion(deliveryRef));
        return deliveryRound.addDelivery(new DeliveryService().deliveryById(deliveryId));
    }
    public boolean removeDelivery(String documentId, String deliveryId) throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = this.deliveryRoundById(documentId);

        DocumentReference deliveryRef = deliveryRoundsCollection.document(deliveryRound.getDocumentId());
        deliveryRoundsCollection
                .document(documentId)
                .update("deliveries", FieldValue.arrayRemove(deliveryRef));
        return deliveryRound.removeDelivery(deliveryId);
    }

}
