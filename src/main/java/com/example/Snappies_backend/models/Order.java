package com.example.Snappies_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Order {


    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime date;

    private String client;

    @ManyToOne(fetch = FetchType.LAZY )
    private User driver;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Article> articles = new ArrayList<>();


    public Order(LocalDateTime date, String client, User driver) {
        this.date = date;
        this.client = client;
        this.driver = driver;
    }


}
