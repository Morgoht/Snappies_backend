package org.example.controllers;

import org.example.models.OrderLine;
import org.example.models.Daycare;
import org.example.models.Order;
import org.example.services.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
@Controller
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service){
        this.service = service;
    }

    @QueryMapping
    public Order orderById(@Argument String orderId) throws ExecutionException, InterruptedException {
        return service.orderById(orderId);
    }

    @QueryMapping
    public List<Order> allorders() throws ExecutionException, InterruptedException {
        return service.allOrders();
    }


    @MutationMapping
    public Order createorder(@Argument List<OrderLine> orderLines, @Argument Daycare daycare) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setDocumentId(UUID.randomUUID().toString());
        order.setOrderLine(orderLines);
        order.setDaycare(daycare);
        service.createOrder(order);
        return order;
    }

    @MutationMapping
    public Order updateorder(@Argument Daycare daycare,
                                 @Argument List<OrderLine> orderLines) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setDocumentId(UUID.randomUUID().toString());
        order.setDaycare(daycare);
        order.setOrderLine(orderLines);
        service.updateOrder(order);
        return order;
    }




    @MutationMapping
    public String deleteorder(@Argument String orderId){
        return service.deleteOrder(orderId);
    }

}
