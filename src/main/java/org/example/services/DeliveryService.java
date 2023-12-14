package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class DeliveryService {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    CollectionReference deliveriesCollection = dbFirestore.collection("deliveries");

    public Delivery setDeliveryFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            Delivery delivery = new Delivery();
            delivery.setDocumentId(doc.getId());

            //Instanciate required Services
            OrderService orderService = new OrderService();

            //Find the article by the reference from Firebase and set
            delivery.setOrder(orderService.orderById(UtilService.findByReference(doc,"order")));
            delivery.setDelivered((Boolean) doc.get("delivered"));

            return delivery;
        } else {
            return null;
        }
    }


    public Delivery deliveryById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = deliveriesCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setDeliveryFromDocumentSnapshot(document);

    }


    public List<Delivery> allDeliveries() throws ExecutionException, InterruptedException {
        CollectionReference collection = deliveriesCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<Delivery> deliveryList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            deliveryList.add(setDeliveryFromDocumentSnapshot(doc));
        }
        return deliveryList;
    }

    public List<Delivery> allDeliveriesBatched() throws ExecutionException, InterruptedException {
        CollectionReference collection = deliveriesCollection;
        int batchSize = 50; // Set your preferred batch size
        List<Delivery> deliveries = new ArrayList<>();
        // Start query with initial batch
        Query query = collection.limit(batchSize);
        while (true) {
            // Fetch the documents in batches
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                Delivery delivery = setDeliveryFromDocumentSnapshot(doc);
                if (delivery != null) {
                    deliveries.add(delivery);
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

        return deliveries;
    }

    public Delivery createDelivery(Delivery delivery, String orderId) throws ExecutionException, InterruptedException {
        DocumentReference docRef =deliveriesCollection.document(delivery.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(delivery);
        DocumentReference orderRef = dbFirestore.collection("orders").document(orderId);
        deliveriesCollection
                .document(delivery.getDocumentId())
                .update("order", orderRef);

        return deliveryById(docRef.getId());
    }


    public Delivery updateDelivery(String deliveryId, String orderId) throws ExecutionException, InterruptedException {
        Delivery delivery = this.deliveryById(deliveryId);
        delivery.setOrder(new OrderService().orderById(orderId));
        deliveriesCollection
                .document(deliveryId)
                .update("order", orderId);
        return delivery;
    }

    public void resetDelivery(String deliveryId) throws ExecutionException, InterruptedException {
        Delivery delivery = this.deliveryById(deliveryId);
        delivery.setDelivered(false);
        deliveriesCollection
                .document(deliveryId)
                .update("delivered", false);
        new OrderService().resetOrder(delivery.getOrder().getDocumentId());
    }

    public void closeDelivery(String deliveryId) throws ExecutionException, InterruptedException {
        Delivery delivery = this.deliveryById(deliveryId);
        delivery.setDelivered(true);
        deliveriesCollection
                .document(deliveryId)
                .update("delivered", true);
    }
    public void openDelivery(String deliveryId) throws ExecutionException, InterruptedException {
        Delivery delivery = this.deliveryById(deliveryId);
        delivery.setDelivered(false);
        deliveriesCollection
                .document(deliveryId)
                .update("delivered", false);
    }


    public String deleteDelivery(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = deliveriesCollection.document(documentId).delete();
        return "Successfully deleted delivery";
    }

}
