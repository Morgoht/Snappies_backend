package org.example.controllers;

import org.example.services.UserService;
import org.example.models.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Controller
public class UserController {

    public UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @QueryMapping
    public User userById(@Argument String userId) throws ExecutionException, InterruptedException {
        System.out.println(userId);
        return service.getUser(userId);
    }


    @GetMapping("/test")
    public ResponseEntity<String> testGetEndpoint(){
        return ResponseEntity.ok("Test Endpoint Get is working");
    }
}
