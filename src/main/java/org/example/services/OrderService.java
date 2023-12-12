package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firestore.v1.Document;
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

    public Order setOrderFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            Order order = new Order();
            order.setDocumentId(doc.getId());

            //Instanciate required Services
            DaycareService daycareService = new DaycareService();
            OrderLineService orderLineService = new OrderLineService();

            //Find the daycare by the reference from Firebase and set the Order.Daycare
            order.setDaycare(daycareService.daycareById(UtilService.findByReference(doc,"daycare")));

            //Find and fill the Orderlines table with Orderlines
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
        DocumentReference documentReference = dbFirestore.collection("orders").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setOrderFromDocumentSnapshot(document);
    }


    public List<Order> allOrders() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("orders");

        // Récupérer tous les documents de la collection "orders"
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        QuerySnapshot snapshot = querySnapshot.get();

        // Initialiser la liste d'orders
        List<Order> orders = new ArrayList<>();

        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            orders.add(setOrderFromDocumentSnapshot(document));
        }

        return orders;
    }

    public Order createOrder(Order order, String daycareId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        dbFirestore.collection("orders")
                .document(order.getDocumentId())
                .update("daycare", daycareRef);

        return order;
    }

    public Order updateOrder(String orderId, String daycareId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(orderId);
        order.setDaycare(new DaycareService().daycareById(daycareId));
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        dbFirestore.collection("orders")
                .document(orderId)
                .update("daycare", daycareRef);
        return order;
    }


    public String deleteOrder(String documentId){
        dbFirestore.collection("orders").document(documentId).delete();
        return "Successfully deleted order";
    }

    public Order addOrderLine(String documentId, OrderLine orderLine) throws ExecutionException, InterruptedException {
        Order order = this.orderById(documentId);
        order.addOrderLine(orderLine);
        DocumentReference orderLineRef = dbFirestore.collection("orderLines").document(orderLine.getDocumentId());
        dbFirestore.collection("orders")
                .document(documentId)
                .update("orderLines", FieldValue.arrayUnion(orderLineRef));
        return order;
    }


    public Order removeOrderLine(String documentId, String orderLineId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(documentId);
        order.removeOrderLine(orderLineId);
        DocumentReference orderLineRef = dbFirestore.collection("orderLines").document(orderLineId);
        dbFirestore.collection("orders")
                .document(documentId)
                .update("orderLines", FieldValue.arrayRemove(orderLineRef));
        return order;
    }
}
