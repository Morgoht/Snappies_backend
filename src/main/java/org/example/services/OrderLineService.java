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
        DocumentReference documentReference = dbFirestore.collection("orderLines").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        OrderLine orderLine = new OrderLine();
        orderLine.setDocumentId(document.getId());
        orderLine.setQuantity(Math.toIntExact((Long) document.get("quantity")));
        //Find the Article using the reference ID from firebase and set it ti the JAVA Article
        ArticleService articleService = new ArticleService();
        orderLine.setArticle(articleService.articleById(findArticleReference(document)));

        return orderLine;

    }

    public String findArticleReference(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        DocumentReference ref = (DocumentReference) doc.get("article");
        assert ref != null;
        ApiFuture<DocumentSnapshot> future = ref.get();
        DocumentSnapshot document = future.get();
        return document.getId();

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
