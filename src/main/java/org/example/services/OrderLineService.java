package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.Article;
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

    public String createOrderLine(OrderLine orderLine, String articleId) throws ExecutionException, InterruptedException {

        DocumentReference docRef = dbFirestore.collection("orderLines").document(orderLine.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(orderLine);
        DocumentReference articleRef = dbFirestore.collection("articles").document(articleId);
        docRef.update("article", articleRef);
        return collectionsApiFuture.get().getUpdateTime().toString();

    }

    public OrderLine updateOrderLine(String orderLineId,double quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = this.orderLineById(orderLineId);
        double backupQuantity = orderLine.getQuantity();
        orderLine.setQuantity(quantity);
        DocumentReference docRef = dbFirestore.collection("orderLines").document(orderLineId);
        docRef.update("quantity",quantity);
        createOrderLineBackup(orderLineId,backupQuantity,quantity);
        showMessageDialog(null,"Updating order with ID :"+orderLineId+"\n quantity before :"+backupQuantity+"\n quantity after :"+quantity);
        return orderLine;
        //Create a new object of type Change in the collection changes saving the orderLineId, the original quantity and the new quantity

    }

    public void createOrderLineBackup(String orderlineId, double backupQuantity, double newQuantity) throws ExecutionException, InterruptedException {
        Update update = new Update();
        update.setDocumentId(orderlineId+"Updated");
        update.setUpdatedOrderId(orderlineId);
        update.setBackupQuantity(backupQuantity);
        update.setNewQuantity(newQuantity);
        DocumentReference docRef = dbFirestore.collection("orderLineUpdates").document(update.getDocumentId());
        ApiFuture<WriteResult> collectionsApiFuture = docRef.set(update);
        collectionsApiFuture.get();
    }

    public void resetOrderLine(String orderlineId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = dbFirestore.collection("orderLineUpdates").document(orderlineId+"Updated");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        double backupQuantity;
        if(document.get("quantity").getClass().equals(Long.class)){
            backupQuantity= ((Long)document.get("quantity")).doubleValue();
        }else{
            backupQuantity =  (Double) document.get("backupQuantity");
        }
        updateOrderLine(orderlineId,backupQuantity);
    }

    public String deleteOrderLine(String documentId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = dbFirestore.collection("orderLines").document(documentId).delete();
       return ("Update time : " + writeResult.get().getUpdateTime());
    }
}
