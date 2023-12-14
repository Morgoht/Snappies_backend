package org.example.models;

import lombok.Data;

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

    public boolean removeOrderLine(String orderLineId){
        OrderLine toRemove = orderLines.stream().filter(e->e.getDocumentId().equals(orderLineId)).findFirst().get();
        return orderLines.remove(toRemove);
    }

}
