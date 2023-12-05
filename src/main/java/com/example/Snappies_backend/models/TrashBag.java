package com.example.Snappies_backend.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TrashBag extends Article{
    public TrashBag(String type) {
        super("sac-poubelle",1);
    }
}
