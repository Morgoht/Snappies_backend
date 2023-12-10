package org.example.controllers;

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
    public Delivery createDelivery(@Argument User driver, @Argument Order order) throws ExecutionException, InterruptedException {
        Delivery delivery = new Delivery();
        delivery.setDocumentId(UUID.randomUUID().toString());
        delivery.setDriver(driver);
        delivery.setOrder(order);
        service.createDelivery(delivery);
        return delivery;
    }

    @MutationMapping
    public Delivery updateDelivery(@Argument Order order,
                                 @Argument User driver, @Argument boolean delivered) throws ExecutionException, InterruptedException {
        Delivery delivery = new Delivery();
        delivery.setDocumentId(UUID.randomUUID().toString());
        delivery.setDriver(driver);
        delivery.setOrder(order);
        delivery.setDelivered(delivered);
        service.updateDelivery(delivery);
        return delivery;
    }


    @MutationMapping
    public String deleteDelivery(@Argument String deliveryId){
        return service.deleteDelivery(deliveryId);
    }


}
