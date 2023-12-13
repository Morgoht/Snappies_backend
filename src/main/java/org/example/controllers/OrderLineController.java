package org.example.controllers;

import org.example.models.Article;
import org.example.models.Order;
import org.example.models.OrderLine;
import org.example.services.ArticleService;
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
    public String createOrderLine(@Argument String articleId, @Argument double quantity) throws ExecutionException, InterruptedException {
        OrderLine orderLine = new OrderLine();
        orderLine.setDocumentId(UUID.randomUUID().toString());
        orderLine.setQuantity(quantity);
        return service.createOrderLine(orderLine, articleId);
    }

    @MutationMapping
    public OrderLine updateOrderLine(@Argument String orderLineId,@Argument String articleId, @Argument double quantity) throws ExecutionException, InterruptedException {
        return service.updateOrderLine(orderLineId, quantity);
    }



    @MutationMapping
    public String deleteOrderLine(@Argument String orderLineId) throws ExecutionException, InterruptedException {
        return service.deleteOrderLine(orderLineId);
    }

}
