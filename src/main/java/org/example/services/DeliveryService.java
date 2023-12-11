package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Delivery;
import org.example.models.OrderLine;
import org.example.models.User;
import org.example.models.Order;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryService {
    Firestore dbFirestore = FirestoreClient.getFirestore();


    public Delivery setDeliveryFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            Delivery delivery = new Delivery();
            delivery.setDocumentId(doc.getId());

            //Instanciate required Services
            OrderService orderService = new OrderService();
            UserService userService = new UserService();

            //Find the article by the reference from Firebase and set
            delivery.setOrder(orderService.orderById(UtilService.findByReference(doc,"order")));
            delivery.setDriver(userService.userById(UtilService.findByReference(doc,"driver")));
            delivery.setDelivered((Boolean) doc.get("delivered"));

            return delivery;
        } else {
            return null;
        }
    }


    public Delivery deliveryById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("deliveries").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setDeliveryFromDocumentSnapshot(document);

    }


    public List<Delivery> allDeliveries() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("deliveries");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<Delivery> deliveryList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            deliveryList.add(setDeliveryFromDocumentSnapshot(doc));
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
