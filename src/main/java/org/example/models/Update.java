package org.example.models;

import lombok.Data;

@Data
public class Update {
    private String documentId;
    private String updatedOrderId;
    private double backupQuantity;
    private double newQuantity;
}