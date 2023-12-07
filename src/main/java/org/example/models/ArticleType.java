package org.example.models;


import lombok.Data;

@Data
public class ArticleType {
    String documentId;
    String name;
    int reserve;
    private String storageType;
}
