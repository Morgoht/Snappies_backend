package com.example.Snappies_backend.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class WashCloth extends Article{
    public WashCloth(String type) {

        super("gant de toilette",3);
    }
}
