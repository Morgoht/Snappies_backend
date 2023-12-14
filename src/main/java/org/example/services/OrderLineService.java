package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Article;
import org.example.models.OrderLine;
import org.example.models.OrderLine;

import org.example.models.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.showMessageDialog;

@Service
public class OrderLineService {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    
    CollectionReference orderLinesCollection = dbFirestore.collection("orderLines");

    public OrderLine setOrderLineFromDocumentSnapshot(DocumentSnapshot doc) throws ExecutionException, InterruptedException {
        if (doc.exists()) {
            //Create Order and set ID
            OrderLine orderLine = new OrderLine();
            orderLine.setDocumentId(doc.getId());

            //Instanciate required Services
            ArticleService articleService = new ArticleService();
            //Find the article by the reference from Firebase and set
            Article article = articleService.articleById(UtilService.findByReference(doc,"article"));
            orderLine.setArticle(article);
            if(doc.get("quantity").getClass().equals(Long.class)){
                orderLine.setQuantity(((Long)doc.get("quantity")).doubleValue());
            }else{
                orderLine.setQuantity((Double)doc.get("quantity"));
            }
            return orderLine;
        } else {
            return null;
        }
    }


    public OrderLine orderLineById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = orderLinesCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        return setOrderLineFromDocumentSnapshot(document);

    }


    public boolean wasUpdated(String orderLineId) throws ExecutionException, InterruptedException {
        String orderLineUpdateId = orderLineId+"Updated";
        DocumentReference docRef = dbFirestore.collection("orderLineUpdates").document(orderLineUpdateId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return (document!=null);
    }


    public List<OrderLine> allOrderLines() throws ExecutionException, InterruptedException {
        CollectionReference collection = orderLinesCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<OrderLine> orderLineList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            orderLineList.add(setOrderLineFromDocumentSnapshot(doc));
        }
        return orderLineList;
    }

    public List<OrderLine> allOrderLinesBatched() throws ExecutionException, InterruptedException {
        CollectionReference collection = orderLinesCollection;
        int batchSize = 50; // Set your preferred batch size
        List<OrderLine> orderLines = new ArrayList<>();
        // Start query with initial batch
        Query query = collection.limit(batchSize);
        while (true) {
            // Fetch the documents in batches
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                OrderLine orderLine = setOrderLineFromDocumentSnapshot(doc);
                if (orderLine != null) {
                    orderLines.add(orderLine);
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

        return orderLines;
    }

    public OrderLine createOrderLine(OrderLine orderLine, String articleId) throws ExecutionException, InterruptedException {

        DocumentReference docRef = orderLinesCollection.document(orderLine.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(orderLine);
        DocumentReference articleRef = dbFirestore.collection("articles").document(articleId);
        docRef.update("article", articleRef);
        return orderLineById(docRef.getId());

    }

    public OrderLine updateOrderLine(String orderLineId,double quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = this.orderLineById(orderLineId);
        double backupQuantity = orderLine.getQuantity();
        orderLine.setQuantity(quantity);
        DocumentReference docRef = orderLinesCollection.document(orderLineId);
        docRef.update("quantity",quantity);
        createOrderLineBackup(orderLineId,backupQuantity,quantity);
        System.out.println("Updating order with ID :"+orderLineId+"\n quantity before :"+backupQuantity+"\n quantity after :"+quantity);
        return orderLine;
        //Create a new object of type Change in the collection changes saving the orderLineId, the original quantity and the new quantity

    }

    public OrderLine permanentUpdateOrderLine(String orderLineId,double quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = this.orderLineById(orderLineId);
        double backupQuantity = orderLine.getQuantity();
        orderLine.setQuantity(quantity);
        DocumentReference docRef = orderLinesCollection.document(orderLineId);
        docRef.update("quantity",quantity);
        return orderLine;
        //Create a new object of type Change in the collection changes saving the orderLineId, the original quantity and the new quantity

    }

    public void createOrderLineBackup(String orderLineId, double backupQuantity, double newQuantity) throws ExecutionException, InterruptedException {
        Update update = new Update();
        update.setDocumentId(orderLineId+"Updated");
        update.setUpdatedOrderId(orderLineId);
        update.setBackupQuantity(backupQuantity);
        update.setNewQuantity(newQuantity);
        DocumentReference docRef = dbFirestore.collection("orderLineUpdates").document(update.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(update);
        collectionsApiFuture.get();
    }

    public void resetOrderLine(String orderlineId) throws ExecutionException, InterruptedException {
        String orderLineUpdateId = orderlineId+"Updated";
        DocumentReference docRef = dbFirestore.collection("orderLineUpdates").document(orderLineUpdateId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        double backupQuantity;
        Object obj = document.get("backupQuantity");
        if(obj==null){
            System.out.println("There were no changes to the delivery in this round");
        }else {
            if (document.get("backupQuantity").getClass().equals(Long.class)) {
                backupQuantity = ((Long) document.get("backupQuantity")).doubleValue();
            } else {
                backupQuantity = (Double) document.get("backupQuantity");
            }
            updateOrderLine(orderlineId, backupQuantity);
            deleteOrderLineBackup(orderLineUpdateId);
        }
    }

    public String deleteOrderLine(String documentId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = orderLinesCollection.document(documentId).delete();
       return ("Update time : " + writeResult.get().getUpdateTime());
    }

    public String deleteOrderLineBackup(String orderLineUpdateId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = dbFirestore.collection("orderLineUpdates").document(orderLineUpdateId).delete();
        return ("Update time : " + writeResult.get().getUpdateTime());
    }
}
