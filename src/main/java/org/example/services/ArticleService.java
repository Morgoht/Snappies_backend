package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.models.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ArticleService {



    Firestore dbFirestore = FirestoreClient.getFirestore();
    CollectionReference articlesCollection = dbFirestore.collection("articles");

    public Article articleById(String documentId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = articlesCollection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Article article;
        if(document.exists()){
            article = document.toObject(Article.class);
            article.setDocumentId(document.getId());
            return article;
        }
        return null;

    }
    public List<Article> allArticles() throws ExecutionException, InterruptedException {
        CollectionReference collection = articlesCollection;
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<Article> arrayList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Article article = doc.toObject(Article.class);
            assert article != null;
            article.setDocumentId(doc.getId());
            arrayList.add(article);
        }
        return arrayList;
    }

    public String createArticle(Article article) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = articlesCollection.document(article.getDocumentId()).set(article);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public Article updateArticle(String articleId, String name,  int reserve, String storageType) throws ExecutionException, InterruptedException {
        Article articleToUpdate = this.articleById(articleId);
        DocumentReference docRef = articlesCollection.document(articleId);

        if (name!= null) {
            docRef.update("name", name);
            articleToUpdate.setName(name);
        }

        if (reserve != 0) {
            docRef.update("reserve",reserve);
            articleToUpdate.setReserve(reserve);
        }

        if (storageType!= null) {
            docRef.update("storageType", storageType);
            articleToUpdate.setStorageType(storageType);
        }

        return articleToUpdate;
    }


    public String deleteArticle(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = articlesCollection.document(documentId).delete();
        return "Successfully deleted article";
    }
    
}
