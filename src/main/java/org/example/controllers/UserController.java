package org.example.controllers;

import org.example.services.UserService;
import org.example.models.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class UserController {

    public UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @QueryMapping
    public User userById(@Argument String userId) throws ExecutionException, InterruptedException {
        return service.userById(userId);
    }

    @QueryMapping
    public List<User> allUsers() throws ExecutionException, InterruptedException {
        return service.allUsers();
    }


    @MutationMapping
    public User  createUser(@Argument String name, @Argument String lastname,
                           @Argument String username, @Argument String email, @Argument String password) throws ExecutionException, InterruptedException {
        User user = new User();
        user.setDocument_id(UUID.randomUUID().toString());
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        service.createUser(user);
        return user;
    }


    @MutationMapping
    public User updateUser(@Argument String name, @Argument String lastname,
                            @Argument String username, @Argument String email, @Argument String password) throws ExecutionException, InterruptedException {
        User user = new User();
        user.setDocument_id(UUID.randomUUID().toString());
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        service.createUser(user);
        return user;

    }



        @GetMapping("/test")
    public ResponseEntity<String> testGetEndpoint(){
        return ResponseEntity.ok("Test Endpoint Get is working");
    }
}
