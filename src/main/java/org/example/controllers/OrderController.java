package org.example.controllers;

import org.example.models.Delivery;
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
    public List<Order> allOrders() throws ExecutionException, InterruptedException {
        return service.allOrders();
    }


    @MutationMapping
    public Order createOrder(@Argument List<OrderLine> orderLines, @Argument Daycare daycare) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setDocumentId(UUID.randomUUID().toString());
        order.setOrderLines(orderLines);
        order.setDaycare(daycare);
        service.createOrder(order);
        return order;
    }

    @MutationMapping
    public Order updateOrder(@Argument Daycare daycare,
                                 @Argument List<OrderLine> orderLines) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setDocumentId(UUID.randomUUID().toString());
        order.setDaycare(daycare);
        order.setOrderLines(orderLines);
        service.updateOrder(order);
        return order;
    }

    @MutationMapping
    public Order addOrderLine(@Argument String documentId, @Argument OrderLine orderLine) throws ExecutionException, InterruptedException {
        return service.addOrderLine(documentId,orderLine);
    }




    @MutationMapping
    public String deleteOrder(@Argument String orderId){
        return service.deleteOrder(orderId);
    }

}
