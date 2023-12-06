package org.example.models;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    private String document_id;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String password;
}
