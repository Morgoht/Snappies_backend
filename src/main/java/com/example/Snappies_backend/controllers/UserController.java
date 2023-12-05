package com.example.Snappies_backend.controllers;


import com.example.Snappies_backend.models.User;
import com.example.Snappies_backend.repositories.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {


    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @QueryMapping
    Iterable<User> users() {
        return userRepository.findAll();
    }

    @MutationMapping
    User createUser(@Argument UserInput user) {
        User u = new User(user.name, user.lastname, user.username, user.email, user.password);
        return userRepository.save(u);
    }

    record UserInput(String name, String lastname, String username, String email, String password) {
    }
}
