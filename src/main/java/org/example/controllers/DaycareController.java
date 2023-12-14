package org.example.controllers;

import org.checkerframework.checker.units.qual.A;
import org.example.models.Daycare;
import org.example.services.DaycareService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
    public Daycare createDaycare(@Argument String address, @Argument String name, @Argument String phoneNumber, @Argument String email) throws ExecutionException, InterruptedException {
        Daycare daycare = new Daycare();
        daycare.setDocumentId(UUID.randomUUID().toString());
        daycare.setAddress(address);
        daycare.setName(name);
        daycare.setPhoneNumber(phoneNumber);
        daycare.setEmail(email);
        service.createDaycare(daycare);
        return daycare;
    }


    @MutationMapping
    public Daycare  updateDaycare(@Argument String daycareId,@Argument String address, @Argument String name, @Argument String phoneNumber, @Argument String email) throws ExecutionException, InterruptedException {
        return service.updateDaycare(daycareId,address,name,phoneNumber,email);
    }

    @MutationMapping
    public String deleteDaycare(@Argument String daycareId){
        return service.deleteDaycare(daycareId);
    }



}
