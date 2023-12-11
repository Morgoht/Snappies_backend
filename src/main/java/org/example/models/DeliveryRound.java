package org.example.models;

import lombok.Data;
import org.example.services.DeliveryService;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeliveryRound {
    private String documentId;
    private String name;
    private List<Delivery> deliveries = new ArrayList<>();
    private boolean roundEnded;



    public boolean addDelivery(Delivery delivery){
        return deliveries.add(delivery);
    }

    public boolean removeDelivery(String deliveryId){
        Delivery toRemove = deliveries.stream().filter(e->e.getDocumentId().equals(deliveryId)).findFirst().get();
        return deliveries.remove(toRemove);
    }
}
