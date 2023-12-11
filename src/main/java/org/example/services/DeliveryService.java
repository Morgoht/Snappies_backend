package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Delivery;
import org.example.models.User;
import org.example.models.Order;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryService {
    Firestore dbFirestore = FirestoreClient.getFirestore();

    public Delivery deliveryById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("deliveries").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Delivery delivery = new Delivery();

        delivery.setDocumentId(document.getId());

        //Find the User using the reference ID from Firebase and set it to the JAVA Delivery
        DocumentReference documentReferenceUser = (DocumentReference) document.get("users");
        assert documentReferenceUser != null;
        ApiFuture<DocumentSnapshot> futureUser = documentReferenceUser.get();
        DocumentSnapshot typeDocUser = futureUser.get();
        User driver = typeDocUser.toObject(User.class);

        delivery.setDriver(driver);

        //Find the Order using reference ID fromp Firebase and set it to te JAVA Delivery
        DocumentReference documentReferenceOrder = (DocumentReference) document.get("order");
        assert documentReferenceUser != null;
        ApiFuture<DocumentSnapshot> futureOrder = documentReferenceUser.get();
        DocumentSnapshot typeDocOrder = futureOrder.get();
        Order order = typeDocOrder.toObject(Order.class);

        delivery.setOrder(order);


        return delivery;

    }


    public List<Delivery> allDeliveries() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("deliveries");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<Delivery> deliveryList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Delivery delivery = new Delivery();
            delivery.setDocumentId(doc.getId());

            //Find the User using the reference ID from Firebase and set it to the JAVA Delivery
            DocumentReference documentReferenceUser = (DocumentReference) doc.get("users");
            assert documentReferenceUser != null;
            ApiFuture<DocumentSnapshot> futureUser = documentReferenceUser.get();
            DocumentSnapshot typeDocUser = futureUser.get();
            User driver = typeDocUser.toObject(User.class);

            delivery.setDriver(driver);

            //Find the Order using reference ID fromp Firebase and set it to te JAVA Delivery
            DocumentReference documentReferenceOrder = (DocumentReference) doc.get("order");
            assert documentReferenceUser != null;
            ApiFuture<DocumentSnapshot> futureOrder = documentReferenceUser.get();
            DocumentSnapshot typeDocOrder = futureOrder.get();
            Order order = typeDocOrder.toObject(Order.class);
            delivery.setOrder(order);

            deliveryList.add(delivery);

        }

        return deliveryList;
    }

    public String createDelivery(Delivery delivery) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("deliveries").document(delivery.getDocumentId()).set(delivery);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateDelivery(Delivery delivery) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture;
        dbFirestore.collection("deliveries").document(delivery.getDocumentId()).set(delivery);
        collectionsApiFuture = dbFirestore.collection("deliveries").document(delivery.getDocumentId()).set(delivery);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }


    public String deleteDelivery(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("deliveries").document(documentId).delete();
        return "Successfully deleted delivery";
    }
}
