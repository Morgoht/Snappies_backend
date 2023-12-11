package org.example.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.checkerframework.checker.units.qual.A;
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
        Article article;
        if(document.exists()){
            article = document.toObject(Article.class);
            article.setDocumentId(document.getId());
            return article;
        }
        /*Article article = new Article();

        article.setDocumentId(document.getId());

        //Find the ArticleType using the reference ID from Firebase and set it to the JAVA ArticleType
        DocumentReference documentReferenceArticleType = (DocumentReference) document.get("articleType");
        assert documentReferenceArticleType != null;
        ApiFuture<DocumentSnapshot> futureArticleType = documentReferenceArticleType.get();
        DocumentSnapshot typeDoc = futureArticleType.get();
        ArticleType type = typeDoc.toObject(ArticleType.class);

        article.setArticleType(type);
        */
        return null;

    }


    public List<Article> allArticles() throws ExecutionException, InterruptedException {
        CollectionReference collection = dbFirestore.collection("articles");
        ApiFuture<QuerySnapshot> querySnapshot = collection.get();
        List<Article> arrayList = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Article article = doc.toObject(Article.class);
            assert article != null;
            article.setDocumentId(doc.getId());
            arrayList.add(article);
            arrayList.add(article);
        }
        /*
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Article article = new Article();
            article.setDocumentId(doc.getId());

            //Find the ArticleType using the reference ID from Firebase and set it to the JAVA ArticleType
            DocumentReference documentReferenceArticleType = (DocumentReference) doc.get("articleType");
            assert documentReferenceArticleType != null;
            ApiFuture<DocumentSnapshot> futureArticleType = documentReferenceArticleType.get();
            DocumentSnapshot typeDoc = futureArticleType.get();
            ArticleType type = typeDoc.toObject(ArticleType.class);

            article.setArticleType(type);
             */
        return arrayList;
    }

    public String createArticle(Article article) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String updateArticle(Article article) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> collectionsApiFuture;
            dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
            collectionsApiFuture = dbFirestore.collection("articles").document(article.getDocumentId()).set(article);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }


    public String deleteArticle(String documentId){
        ApiFuture<WriteResult> writeResultApiFuture = dbFirestore.collection("articles").document(documentId).delete();
        return "Successfully deleted article";
    }
    
}
