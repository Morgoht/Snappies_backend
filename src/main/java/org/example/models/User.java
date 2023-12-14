package org.example.models;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    private String documentId;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
}
