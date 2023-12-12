package org.example.controllers;

import org.example.models.*;
import org.example.services.ArticleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service){
        this.service = service;
    }

    @QueryMapping
    public Article articleById(@Argument String articleId) throws ExecutionException, InterruptedException {
        return service.articleById(articleId);
    }

    @QueryMapping
    public List<Article> allArticles() throws ExecutionException, InterruptedException {
        return service.allArticles();
    }


    @MutationMapping
    public String createArticle(@Argument String name, @Argument int reserve,@Argument String storageType) throws ExecutionException, InterruptedException {
        Article article = new Article();
        article.setDocumentId(UUID.randomUUID().toString());
        article.setName(name);
        article.setReserve(reserve);
        article.setStorageType(storageType);
        return service.createArticle(article);
    }

    @MutationMapping
    public Article updateArticle(@Argument String articleId, @Argument String name, @Argument int reserve,@Argument String storageType) throws ExecutionException, InterruptedException {
        return service.updateArticle(articleId,name,reserve,storageType);
    }

    @MutationMapping
    public String deleteArticle(@Argument String articleId){
        return service.deleteArticle(articleId);
    }



}
