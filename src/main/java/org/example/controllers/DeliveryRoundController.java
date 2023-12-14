package org.example.controllers;

import org.example.models.Delivery;
import org.example.models.DeliveryRound;
import org.example.models.Order;
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
        return service.allDeliveryRoundsBatched();
    }

    @QueryMapping
    public DeliveryRound deliveryRoundById(@Argument String deliveryRoundId) throws ExecutionException, InterruptedException {
        return service.deliveryRoundById(deliveryRoundId);
    }

    @MutationMapping
    public String createDeliveryRound(@Argument String name, @Argument String driverId) throws ExecutionException, InterruptedException {
        DeliveryRound deliveryRound = new DeliveryRound();
        deliveryRound.setDocumentId(UUID.randomUUID().toString());
        deliveryRound.setName(name);
        return service.createDeliveryRound(deliveryRound, driverId);
    }

    @MutationMapping
    public DeliveryRound updateDeliveryRound(@Argument String deliveryRoundId, @Argument String name, @Argument String driverId) throws ExecutionException, InterruptedException {
        return service.updateDeliveryRound(deliveryRoundId,name,driverId);
    }

    @MutationMapping
    public String endRound(@Argument String deliveryRoundId) throws ExecutionException, InterruptedException {
        return service.endRound(deliveryRoundId);
    }
    @MutationMapping
    public String restartRound(@Argument String deliveryRoundId) throws ExecutionException, InterruptedException {
        return service.restartRound(deliveryRoundId);
    }
    @MutationMapping
    public String deleteDeliveryRound(@Argument String deliveryRoundId) {
        return service.deleteDeliveryRound(deliveryRoundId);
    }

    @MutationMapping
    public boolean addDelivery(@Argument String deliveryRoundId, @Argument String deliveryId) throws ExecutionException, InterruptedException {
        return service.addDelivery(deliveryRoundId,deliveryId);
    }

    @MutationMapping
    public boolean removeDelivery(@Argument String documentId, @Argument String deliveryId) throws ExecutionException, InterruptedException {
        return service.removeDelivery(documentId,deliveryId);
    }
}
