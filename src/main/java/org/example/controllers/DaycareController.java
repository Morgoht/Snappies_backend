package org.example.controllers;

import org.example.models.Daycare;
import org.example.services.DaycareService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class DaycareController {
    private final DaycareService service;

    private DaycareController(DaycareService service){
        this.service = service;
    }

    @QueryMapping
    public Daycare daycareById(@Argument String daycareId) throws ExecutionException, InterruptedException {
        return service.daycareById(daycareId);
    }

    @QueryMapping
    public List<Daycare> allDaycares() throws ExecutionException, InterruptedException {
        return service.allDaycares();
    }


    @MutationMapping
    public Daycare  createDaycare(@Argument String name, @Argument String email, @Argument String address,
                            @Argument String phoneNumber) throws ExecutionException, InterruptedException {
        Daycare daycare = new Daycare();
        daycare.setDocumentId(UUID.randomUUID().toString());
        daycare.setName(name);
        daycare.setEmail(email);
        daycare.setAddress(address);
        daycare.setPhoneNumber(phoneNumber);
        service.createDaycare(daycare);
        return daycare;
    }


    @MutationMapping
    public Daycare  updateDaycare(@Argument String name, @Argument String email, @Argument String address,
                                  @Argument String phoneNumber) throws ExecutionException, InterruptedException {
        Daycare daycare = new Daycare();
        daycare.setDocumentId(UUID.randomUUID().toString());
        daycare.setName(name);
        daycare.setEmail(email);
        daycare.setAddress(address);
        daycare.setPhoneNumber(phoneNumber);
        service.updateDaycare(daycare);
        return daycare;

    }

    @MutationMapping
    public String deleteDaycare(@Argument String daycareId){
        return service.deleteDaycare(daycareId);
    }



}
