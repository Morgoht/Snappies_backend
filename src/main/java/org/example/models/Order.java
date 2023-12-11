package org.example.models;

import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private String documentId;
    private List<OrderLine> orderLines = new ArrayList<>();
    private Daycare daycare;

    public boolean addOrderLine(OrderLine line){
        return orderLines.add(line);
    }
}
