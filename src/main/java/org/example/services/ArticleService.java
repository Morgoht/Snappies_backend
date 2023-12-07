package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ArticleService {



    Firestore dbFirestore = FirestoreClient.getFirestore();

    public Article articleById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = dbFirestore.collection("articles").document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            Map<String, Object> data = document.getData();
            if (data != null && data.containsKey("type")) {
                String articleType = (String) data.get("type");
                return switch (articleType) {
                    case "Lange" -> document.toObject(Lange.class);
                    case "Inserts" -> document.toObject(Insert.class);
                    case "SacPoubelle" -> document.toObject(SacPoubelle.class);
                    case "GantDeToilette" -> document.toObject(GantDeToilette.class);
                    default -> null;
                };
            }
        }
        return null;
    }

    public List<Article> allArticles() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("articles");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();

        List<Article> articleList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            if(doc.get("type").equals("Lange")){
                Lange article = doc.toObject(Lange.class);
                articleList.add(article);
            }
            else if (doc.get("type").equals("Sac poubelle")) {
                SacPoubelle article = doc.toObject(SacPoubelle.class);
                articleList.add(article);
            }
            else if (doc.get("type").equals("Insert")) {
                Insert article = doc.toObject(Insert.class);
                articleList.add(article);
            }
            else if (doc.get("type").equals("Gant de toilette")) {
                GantDeToilette article = doc.toObject(GantDeToilette.class);
                articleList.add(article);
            }
        }
        return articleList;
    }

    public String createArticle(Article article, String size) throws ExecutionException, InterruptedException {
        String articleType = article.getType();
        ApiFuture<WriteResult> collectionsApiFuture;
        // If the article type is Lange, set the size before saving
        if ("Lange".equals(articleType) && article instanceof Lange lange) {
            lange.setSize(size);
            collectionsApiFuture = dbFirestore.collection("articles").document(lange.getDocumentId()).set(lange);
        } else {
            // For other article types or if article type is unknown, save as-is
            dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
            collectionsApiFuture = dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
        }
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateArticle(Article article, String size) throws ExecutionException, InterruptedException {
        String articleType = article.getType();
        ApiFuture<WriteResult> collectionsApiFuture;
        // If the article type is Lange, set the size before saving
        if ("Lange".equals(articleType) && article instanceof Lange lange) {
            lange.setSize(size);
            collectionsApiFuture = dbFirestore.collection("articles").document(lange.getDocumentId()).set(lange);
        } else {
            // For other article types or if article type is unknown, save as-is
            dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
            collectionsApiFuture = dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
        }
        return collectionsApiFuture.get().getUpdateTime().toString();
    }


    public String deleteArticle(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("articles").document(documentId).delete();
        return "Successfully deleted article";
    }
    
}
