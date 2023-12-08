package org.example.controllers;

import org.example.models.Article;
import org.example.models.OrderLine;
import org.example.services.OrderLineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
@Controller
public class OrderLineController {
    private final OrderLineService service;

    public OrderLineController(OrderLineService service){
        this.service = service;
    }

    @QueryMapping
    public OrderLine orderLineById(@Argument String orderLineId) throws ExecutionException, InterruptedException {
        return service.orderLineById(orderLineId);
    }

    @QueryMapping
    public List<OrderLine> allOrderLines() throws ExecutionException, InterruptedException {
        return service.allOrderLines();
    }


    @MutationMapping
    public OrderLine createOrderLine(@Argument Article article, @Argument int quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = new OrderLine();
        orderLine.setDocumentId(UUID.randomUUID().toString());
        orderLine.setArticle(article);
        orderLine.setQuantity(quantity);
        service.createOrderLine(orderLine);
        return orderLine;
    }

    @MutationMapping
    public OrderLine updateOrderLine(@Argument Article article,
                                 @Argument int quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = new OrderLine();
        orderLine.setDocumentId(UUID.randomUUID().toString());
        orderLine.setArticle(article);
        orderLine.setQuantity(quantity);
        service.updateOrderLine(orderLine);
        return orderLine;
    }



    @MutationMapping
    public String deleteOrderLine(@Argument String orderLineId){
        return service.deleteOrderLine(orderLineId);
    }

}
