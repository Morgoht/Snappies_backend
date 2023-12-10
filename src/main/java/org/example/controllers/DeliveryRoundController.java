package org.example.controllers;

import org.example.models.Delivery;
import org.example.models.DeliveryRound;
import org.example.services.DeliveryRoundService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class DeliveryRoundController {
    private final DeliveryRoundService service;

    public DeliveryRoundController(DeliveryRoundService service) {
        this.service = service;
    }


    @QueryMapping
    public List<DeliveryRound> allDeliveryRounds()
            throws ExecutionException, InterruptedException {
        return service.allDeliveryRounds();
    }

    @MutationMapping
    public DeliveryRound createDeliveryRound(@Argument String name)
            throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = new DeliveryRound();
        deliveryRound.setDocumentId(UUID.randomUUID().toString());
        deliveryRound.setName(name);
        // You might need to add more logic here based on your actual data model
        service.createDeliveryRound(deliveryRound);
        return deliveryRound;
    }

    @MutationMapping
    public DeliveryRound updateDeliveryRound(@Argument String deliveryRoundId,
                                             @Argument String name, @Argument boolean roundEnded)
            throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = new DeliveryRound();
        deliveryRound.setDocumentId(deliveryRoundId);
        deliveryRound.setName(name);
        deliveryRound.setRoundEnded(roundEnded);
        // You might need to add more logic here based on your actual data model
        service.updateDeliveryRound(deliveryRound);
        return deliveryRound;
    }

    @MutationMapping
    public String deleteDeliveryRound(@Argument String deliveryRoundId) {
        return service.deleteDeliveryRound(deliveryRoundId);
    }

    @MutationMapping
    public Delivery addDelivery(@Argument String documentId, @Argument Delivery delivery) throws ExecutionException, InterruptedException {
        return service.addDelivery(documentId,delivery);
    }
}
