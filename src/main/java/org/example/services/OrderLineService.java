package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Article;
import org.example.models.OrderLine;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OrderLineService {
    Firestore dbFirestore = FirestoreClient.getFirestore();

    public OrderLine orderLineById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("orderLine").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        OrderLine orderLine = new OrderLine();
        orderLine.setDocumentId(document.getId());
        orderLine.setQuantity((Integer) document.get("quantity"));

        //Find the Article using the reference ID from firebase and set it ti the JAVA Article
        DocumentReference documentReferenceArticle = (DocumentReference) document.get("articles");
        assert documentReferenceArticle != null;
        ApiFuture<DocumentSnapshot> futureArticle = documentReferenceArticle.get();
        DocumentSnapshot articleDoc = futureArticle.get();
        Article article = articleDoc.toObject(Article.class);
        orderLine.setArticle(article);

        return orderLine;

    }


    public List<OrderLine> allOrderLines() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("orderLine");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<OrderLine> orderLineList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            OrderLine orderLine = new OrderLine();
            orderLine.setDocumentId(doc.getId());

            //Find the Article using the reference ID from Firebase and set it to the JAVA OrderLineType
            DocumentReference documentReferenceArticle = (DocumentReference) doc.get("articles");
            assert documentReferenceArticle != null;
            ApiFuture<DocumentSnapshot> futureArticle = documentReferenceArticle.get();
            DocumentSnapshot articleDoc = futureArticle.get();
            Article article = articleDoc.toObject(Article.class);
            orderLine.setArticle(article);

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
