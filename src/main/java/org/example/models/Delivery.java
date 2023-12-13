package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class Delivery {
    private String documentId;
    private Order order;
    private boolean delivered;


}
