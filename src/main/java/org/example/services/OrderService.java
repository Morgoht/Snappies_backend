package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
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

    public Order orderById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("orders").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Order order = new Order();
            order.setDocumentId(document.getId());
            order.setOrderLine(document.toObject(Order.class).getOrderLine());
            // Remarque : vous devrez peut-être ajuster le nom du champ dans votre modèle Order

            // Trouver la Daycare en utilisant la référence depuis Firebase et la définir sur l'objet JAVA Daycare
            DocumentReference documentReferenceDaycare = (DocumentReference) document.get("daycare");
            if (documentReferenceDaycare != null) {
                ApiFuture<DocumentSnapshot> futureDaycare = documentReferenceDaycare.get();
                DocumentSnapshot daycareDoc = futureDaycare.get();

                if (daycareDoc.exists()) {
                    // Convertir le DocumentSnapshot en objet Daycare et le définir sur l'objet Order
                    Daycare daycare = daycareDoc.toObject(Daycare.class);
                    order.setDaycare(daycare);
                }
            }

            return order;
        } else {
            // Le document n'existe pas
            return null;
        }
    }


    public List<Order> allOrders() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("orders");

        // Récupérer tous les documents de la collection "orders"
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        QuerySnapshot snapshot = querySnapshot.get();

        // Initialiser la liste d'orders
        List<Order> orders = new ArrayList<>();

        // Parcourir les documents et les convertir en objets Order
        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            Order order = new Order();
            order.setDocumentId(document.getId());
            order.setOrderLine(document.toObject(Order.class).getOrderLine());
            // Remarque : ajustez le nom du champ dans votre modèle Order

            // Trouver la Daycare en utilisant la référence depuis Firebase et la définir sur l'objet JAVA Daycare
            DocumentReference documentReferenceDaycare = (DocumentReference) document.get("daycare");
            if (documentReferenceDaycare != null) {
                ApiFuture<DocumentSnapshot> futureDaycare = documentReferenceDaycare.get();
                DocumentSnapshot daycareDoc = futureDaycare.get();

                if (daycareDoc.exists()) {
                    // Convertir le DocumentSnapshot en objet Daycare et le définir sur l'objet Order
                    Daycare daycare = daycareDoc.toObject(Daycare.class);
                    order.setDaycare(daycare);
                }
            }

            orders.add(order);
        }

        return orders;
    }

    public String createOrder(Order order) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateOrder(Order order) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture;
        dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        collectionsApiFuture = dbFirestore.collection("orders").document(order.getDocumentId()).set(order);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }


    public String deleteOrder(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("orders").document(documentId).delete();
        return "Successfully deleted order";
    }

    public Order addOrderLine(String documentId, OrderLine orderLine) throws ExecutionException, InterruptedException {

        DocumentReference orderRef = dbFirestore.collection("orders").document(documentId);
        // Récupérez le document actuel
        DocumentSnapshot document = orderRef.get().get();
        if (document.exists()) {
            Order existingOrder = document.toObject(Order.class);
            existingOrder.getOrderLine().add(orderLine);
            orderRef.set(existingOrder, SetOptions.merge());

            return existingOrder;
        } else {
            return null;
        }
    }
}
