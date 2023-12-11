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
    public Article createArticle(@Argument Article article) throws ExecutionException, InterruptedException {
        System.out.println(article.getName());
        article.setDocumentId(UUID.randomUUID().toString());
        service.createArticle(article);
        return article;
    }

    @MutationMapping
    public Article updateArticle(@Argument Article article) throws ExecutionException, InterruptedException {
        service.updateArticle(article);
        return article;
    }




    @MutationMapping
    public String deleteArticle(@Argument String articleId){
        return service.deleteArticle(articleId);
    }



}
