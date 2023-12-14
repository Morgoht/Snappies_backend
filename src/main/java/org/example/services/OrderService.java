package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.v1.Document;
import org.example.controllers.OrderLineController;
import org.example.models.Daycare;
import org.example.models.Order;
import org.example.models.Order;
import org.example.models.OrderLine;
import org.mockito.internal.matchers.Or;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class OrderService {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    CollectionReference ordersCollection = dbFirestore.collection("orders");

    public Order setOrderFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            Order order = new Order();
            order.setDocumentId(doc.getId());
            DaycareService daycareService = new DaycareService();
            OrderLineService orderLineService = new OrderLineService();
            order.setDaycare(daycareService.daycareById(UtilService.findByReference(doc,"daycare")));
            for (String id: UtilService.listOfReferences(doc,"orderLines")
            )   {
                OrderLine orderLine = orderLineService.orderLineById(id);
                if(orderLine==null) {
                    this.removeDeletedOrderLine(doc.getId(), id);
                }
                else order.addOrderLine(orderLine);

            }
            return order;
        } else {
            return null;
        }
    }

    public Order orderById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = ordersCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setOrderFromDocumentSnapshot(document);
    }


    public List<Order> allOrders() throws ExecutionException, InterruptedException {
        CollectionReference collection = ordersCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        QuerySnapshot snapshot = querySnapshot.get();
        List<Order> orders = new ArrayList<>();

        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            orders.add(setOrderFromDocumentSnapshot(document));
        }

        return orders;
    }

    public List<Order> allOrdersBatched() throws ExecutionException, InterruptedException {
        CollectionReference collection = ordersCollection;
        int batchSize = 50; // Set your preferred batch size
        List<Order> orders = new ArrayList<>();
        // Start query with initial batch
        Query query = collection.limit(batchSize);
        while (true) {
            // Fetch the documents in batches
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                Order delivery = setOrderFromDocumentSnapshot(doc);
                if (delivery != null) {
                    orders.add(delivery);
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

        return orders;
    }

    public String createOrder(Order order, String daycareId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = ordersCollection.document(order.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(order);
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        docRef.update("daycare", daycareRef);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Order updateOrder(String orderId, String daycareId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(orderId);
        DocumentReference docRef = ordersCollection.document(order.getDocumentId());
        order.setDaycare(new DaycareService().daycareById(daycareId));
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        docRef.update("daycare", daycareRef);
        return order;
    }

    public String deleteOrder(String documentId){
        ordersCollection.document(documentId).delete();
        return "Successfully deleted order";
    }

    public boolean addOrderLine(String documentId, OrderLine orderLine,String articleId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = dbFirestore.collection("orderLines").document(orderLine.getDocumentId());
        ApiFuture<WriteResult> future = docRef.set(orderLine);
        try {
            // Wait for the completion of the first operation
            future.get(15, TimeUnit.SECONDS);

            DocumentReference articleRef = dbFirestore.collection("articles").document(articleId);
            ApiFuture<WriteResult> futureUpdate = docRef.update("article", articleRef);

            // Wait for the completion of the second operation
            futureUpdate.get();

            Order order = this.orderById(documentId);
            ApiFuture<WriteResult> futureOrderlines = ordersCollection
                    .document(documentId)
                    .update("orderLines", FieldValue.arrayUnion(docRef));

            // Wait for the completion of the third operation
            futureOrderlines.get();
            return order.addOrderLine(orderLine);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // Handle exceptions
        }
           return false;
    }

    public void removeDeletedOrderLine(String documentId, String orderLineId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = dbFirestore.collection("orders").document(documentId);
        // Fetch the document
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            // Get the array of references
            List<DocumentReference> references = (List<DocumentReference>) document.get("orderLines");
            if (references != null) {
                List<DocumentReference> updatedReferences = new ArrayList<>(references);
                // Find and remove the reference with the specified ID
                for (DocumentReference reference : references) {
                    if (reference.getId().equals(orderLineId)) {
                        updatedReferences.remove(reference);
                        break;
                    }
                }
                // Update the document with the modified array
                docRef.update("orderLines", updatedReferences)
                        .get(); // Wait for the update to complete

                System.out.println("Reference deleted from array");
            } else {
                System.out.println("No array field found in the document");
            }
        } else {
            System.out.println("Document does not exist");
        }


       }

    public boolean containsOrderLine(String documentId, String orderLineId)throws ExecutionException, InterruptedException {
        DocumentReference orderLineRef = dbFirestore.collection("orders").document(documentId).collection("orderLines").document(orderLineId);
        ApiFuture<DocumentSnapshot> future = orderLineRef.get();
        DocumentSnapshot document = future.get();
        return document.exists();
    }
    public boolean removeOrderLine(String documentId, String orderLineId) throws ExecutionException, InterruptedException
        {
            Order order = this.orderById(documentId);
            if (containsOrderLine(documentId, orderLineId)) {
                DocumentReference orderLineRef = dbFirestore.collection("orderLines").document(orderLineId);
                ordersCollection
                        .document(documentId)
                        .update("orderLines", FieldValue.arrayRemove(orderLineRef));
                return order.removeOrderLine(orderLineId);
            } else
                return false;
        }

    public void resetOrder(String orderId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(orderId);
        for (OrderLine ol: order.getOrderLines()
             ) {
            new OrderLineService().resetOrderLine(ol.getDocumentId());

        }
    }
}
