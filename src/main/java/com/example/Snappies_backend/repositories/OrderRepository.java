package com.example.Snappies_backend.repositories;

import com.example.Snappies_backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
        Basic CRUD Operations:
        Save an entity: save(S entity)
        Delete an entity by ID: deleteById(ID id)
        Find an entity by ID: findById(ID id)
        Check if an entity exists by ID: existsById(ID id)
        Get all entities: findAll()
        Count entities: count()
     */
}