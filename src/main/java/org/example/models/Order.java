package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String documentId;
    private List<OrderLine> orderLine;
    private Daycare daycare;
}
