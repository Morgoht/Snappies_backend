package org.example.controllers;

import org.example.models.Delivery;
import org.example.models.OrderLine;
import org.example.models.Daycare;
import org.example.models.Order;
import org.example.services.ArticleService;
import org.example.services.OrderLineService;
import org.example.services.OrderService;
import org.mockito.internal.matchers.Or;
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
    public String createOrder(@Argument String daycareId) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setDocumentId(UUID.randomUUID().toString());
        return service.createOrder(order, daycareId);
    }

    @MutationMapping
    public Order updateOrder(@Argument String orderId,@Argument String daycareId) throws ExecutionException, InterruptedException {
        return service.updateOrder(orderId,daycareId);
    }

    @MutationMapping
    public boolean addOrderLine(@Argument String orderId, @Argument String articleId, @Argument int quantity) throws ExecutionException, InterruptedException {
        OrderLine newOrderLine = new OrderLine();
        newOrderLine.setArticle(new ArticleService().articleById(articleId));
        newOrderLine.setQuantity(quantity);
        return service.addOrderLine(orderId,newOrderLine);
    }


    @MutationMapping
    public boolean removeOrderLine(@Argument String documentId, @Argument String orderLineId) throws ExecutionException, InterruptedException {
        return service.removeOrderLine(documentId,orderLineId);
    }



    @MutationMapping
    public String deleteOrder(@Argument String orderId){
        return service.deleteOrder(orderId);
    }

}
