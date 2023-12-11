package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Article;
import org.example.models.Order;
import org.example.models.OrderLine;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OrderLineService {
    Firestore dbFirestore = FirestoreClient.getFirestore();

    public OrderLine setOrderLineFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            OrderLine orderLine = new OrderLine();
            orderLine.setDocumentId(doc.getId());

            //Instanciate required Services
            ArticleService articleService = new ArticleService();
            //Find the article by the reference from Firebase and set
            orderLine.setArticle(articleService.articleById(UtilService.findByReference(doc,"article")));
            orderLine.setQuantity(Math.toIntExact((Long) doc.get("quantity")));
            return orderLine;
        } else {
            return null;
        }
    }


    public OrderLine orderLineById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("orderLines").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setOrderLineFromDocumentSnapshot(document);

    }


    public List<OrderLine> allOrderLines() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("orderLines");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<OrderLine> orderLineList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            orderLineList.add(setOrderLineFromDocumentSnapshot(doc));
        }
        return orderLineList;
    }

    public String createOrderLine(OrderLine orderLine) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("orderLine").document(orderLine.getDocumentId()).set(orderLine);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateOrderLine(OrderLine orderLine) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture;
        dbFirestore.collection("orderLine").document(orderLine.getDocumentId()).set(orderLine);
        collectionsApiFuture = dbFirestore.collection("articles").document(orderLine.getDocumentId()).set(orderLine);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }


    public String deleteOrderLine(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("orderLine").document(documentId).delete();
        return "Successfully deleted orderLine";
    }
}
