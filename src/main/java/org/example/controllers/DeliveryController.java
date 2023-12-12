package org.example.controllers;

import org.checkerframework.checker.units.qual.A;
import org.example.models.Delivery;
import org.example.models.Order;
import org.example.models.User;
import org.example.services.DeliveryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
@Controller
public class DeliveryController {
    private final DeliveryService service;

    public DeliveryController(DeliveryService service){
        this.service = service;
    }

    @QueryMapping
    public Delivery deliveryById(@Argument String deliveryId) throws ExecutionException, InterruptedException {
        return service.deliveryById(deliveryId);
    }

    @QueryMapping
    public List<Delivery> allDeliveries() throws ExecutionException, InterruptedException {
        return service.allDeliveries();
    }


    @MutationMapping
    public String createDelivery(@Argument String orderId) throws ExecutionException, InterruptedException {
        Delivery delivery = new Delivery();
        delivery.setDocumentId(UUID.randomUUID().toString());
        return  service.createDelivery(delivery,orderId);
    }

    @MutationMapping
    public Delivery updateDelivery(@Argument String deliveryId, @Argument String orderId) throws ExecutionException, InterruptedException {
        return service.updateDelivery(deliveryId,orderId);
    }


    @MutationMapping
    public String deleteDelivery(@Argument String deliveryId){
        return service.deleteDelivery(deliveryId);
    }


}
