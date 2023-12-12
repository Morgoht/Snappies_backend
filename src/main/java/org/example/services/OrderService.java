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
import org.example.models.OrderLine;
import org.mockito.internal.matchers.Or;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
            ) {
                order.addOrderLine(orderLineService.orderLineById(id));
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

    public boolean addOrderLine(String documentId, OrderLine orderLine) throws ExecutionException, InterruptedException {
        Order order = this.orderById(documentId);
        new OrderLineController(new OrderLineService()).createOrderLine(orderLine.getArticle().getDocumentId(),orderLine.getQuantity());
        DocumentReference orderLineRef = dbFirestore.collection("orders").document(orderLine.getDocumentId());
        ordersCollection
                .document(documentId)
                .update("orderLines", FieldValue.arrayUnion(orderLineRef));
        return order.addOrderLine(orderLine);
    }


    public boolean removeOrderLine(String documentId, String orderLineId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(documentId);
        DocumentReference orderLineRef = dbFirestore.collection("orderLines").document(orderLineId);
        ordersCollection
                .document(documentId)
                .update("orderLines", FieldValue.arrayRemove(orderLineRef));
        return order.removeOrderLine(orderLineId);
    }
}
