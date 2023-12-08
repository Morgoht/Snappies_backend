package org.example.models;

import lombok.Data;

@Data
public class OrderLine {
    private String documentId;
    private Article article;
    private int quantity;
}
