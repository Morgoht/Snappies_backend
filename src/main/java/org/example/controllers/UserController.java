package org.example.controllers;

import org.checkerframework.checker.units.qual.A;
import org.example.services.UserService;
import org.example.models.User;
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
public class UserController {

    private final UserService service;

    private UserController(UserService service){
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
    public String createUser(@Argument String name, @Argument String lastname,
                           @Argument String username, @Argument String email, @Argument String password,@Argument String phoneNumber) throws ExecutionException, InterruptedException {
        User user = new User();
        user.setDocumentId(UUID.randomUUID().toString());
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        return service.createUser(user);
    }


    @MutationMapping
    public User updateUser(@Argument String userId,@Argument String name, @Argument String lastname,
                           @Argument String username, @Argument String email, @Argument String password,@Argument String phoneNumber) throws ExecutionException, InterruptedException {
        return service.updateUser(userId, name, lastname,username,email,password,phoneNumber);
    }

    @MutationMapping
    public String deleteUser(@Argument String userId){
        return service.deleteUser(userId);
    }



        @GetMapping("/test")
    public ResponseEntity<String> testGetEndpoint(){
        return ResponseEntity.ok("Test Endpoint Get is working");
    }
}
