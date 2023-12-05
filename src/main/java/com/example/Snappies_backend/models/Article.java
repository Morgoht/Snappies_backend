package com.example.Snappies_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public abstract class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // Constructor
    public Article(String type, int price) {
        this.type = type;
        this.price=price;
    }
}
