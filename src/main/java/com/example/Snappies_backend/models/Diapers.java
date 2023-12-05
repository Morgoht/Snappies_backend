package com.example.Snappies_backend.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Diapers extends Article{
    private String size;
    public Diapers(String type, String size) {
        super("langes",5);
        this.size=size;
    }
}
