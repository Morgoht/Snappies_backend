package org.example.controllers;

import org.example.models.*;
import org.example.services.ArticleService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class ArticleController {

    ArticleService service = new ArticleService();

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
    public Article  createArticle(@Argument String type, @Argument double price,
                            @Argument String size) throws ExecutionException, InterruptedException {
             switch (type) {
                case "Lange" ->{
                    Lange article = new Lange();
                article.setType(type);
                article.setPrice(price);
                article.setSize(size);
                    service.createArticle(article,size);
                    return article;
                }
                case "Insert" -> {
                    Insert article = new Insert();
                    article.setType(type);
                    article.setPrice(price);
                    service.createArticle(article,"");
                    return article;
                }
                case "Sac poubelle" -> {
                    SacPoubelle article = new SacPoubelle();
                    article.setType(type);
                    article.setPrice(price);
                    service.createArticle(article,"");
                    return article;
                }
                case "Gant de toilette" -> {
                    GantDeToilette article = new GantDeToilette();
                    article.setType(type);
                    article.setPrice(price);
                    service.createArticle(article,"");
                    return article;
                }
                default -> {
                     return null;
                 }
             }
    }

    @MutationMapping
    public Article  updateArticle(@Argument String type, @Argument double price,
                                  @Argument String size) throws ExecutionException, InterruptedException {
        switch (type) {
            case "Lange" ->{
                Lange article = new Lange();
                article.setType(type);
                article.setPrice(price);
                article.setSize(size);
                service.updateArticle(article,size);
                return article;
            }
            case "Insert" -> {
                Insert article = new Insert();
                article.setType(type);
                article.setPrice(price);
                service.updateArticle(article,"");
                return article;
            }
            case "Sac poubelle" -> {
                SacPoubelle article = new SacPoubelle();
                article.setType(type);
                article.setPrice(price);
                service.updateArticle(article,"");
                return article;
            }
            case "Gant de toilette" -> {
                GantDeToilette article = new GantDeToilette();
                article.setType(type);
                article.setPrice(price);
                service.updateArticle(article,"");
                return article;
            }
            default -> {
                return null;
            }
        }
    }


    @MutationMapping
    public String deleteArticle(@Argument String articleId){
        return service.deleteArticle(articleId);
    }



}
