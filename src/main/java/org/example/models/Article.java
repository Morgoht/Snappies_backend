package org.example.models;

import lombok.Data;

@Data
public class Article {
    String documentId;
    String name;
    int reserve;
    private String storageType;

}
