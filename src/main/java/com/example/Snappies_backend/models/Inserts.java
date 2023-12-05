package com.example.Snappies_backend.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Inserts extends Article{
    public Inserts(String type) {
        super("inserts",4);
    }
}
