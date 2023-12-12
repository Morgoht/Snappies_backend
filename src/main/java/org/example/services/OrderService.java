package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
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
            for (String id: UtilService.listOfReferences(doc,"orderLine")
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

    public String createOrder(Order order, String daycareId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        System.out.println(daycareRef);
        dbFirestore.collection("orders")
                .document(order.getDocumentId())
                .update("daycare", daycareRef);

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Order updateOrder(String orderId, String daycareId) throws ExecutionException, InterruptedException {
        Order order = this.orderById(orderId);
        order.setDaycare(new DaycareService().daycareById(daycareId));
        dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        DocumentReference daycareRef = dbFirestore.collection("daycares").document(daycareId);
        dbFirestore.collection("orders")
                .document(order.getDocumentId())
                .update("daycare", daycareRef);
        return order;
    }


    public String deleteOrder(String documentId){
        dbFirestore.collection("orders").document(documentId).delete();
        return "Successfully deleted order";
    }

    public Order addOrderLine(String documentId, OrderLine orderLine) throws ExecutionException, InterruptedException {
        Order currentOrder = this.orderById(documentId);
        currentOrder.addOrderLine(orderLine);
        dbFirestore.collection("orders").document(currentOrder.getDocumentId()).set(currentOrder);
        return currentOrder;
    }


    public Order removeOrderLine(String documentId, String orderLineId) throws ExecutionException, InterruptedException {
        Order currentOrder = this.orderById(documentId);
        currentOrder.removeOrderLine(orderLineId);
        dbFirestore.collection("orders").document(currentOrder.getDocumentId()).set(currentOrder);
        return currentOrder;
    }
}
