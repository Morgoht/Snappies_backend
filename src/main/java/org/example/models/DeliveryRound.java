package org.example.models;

import lombok.Data;
import java.util.List;

@Data
public class DeliveryRound {
    private String documentId;
    private String name;
    private List<Delivery> deliveries;
}
