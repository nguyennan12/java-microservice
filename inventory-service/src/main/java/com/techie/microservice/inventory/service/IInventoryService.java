package com.techie.microservice.inventory.service;

public interface IInventoryService {
    public boolean isInStock(String skuCode, Integer quantity);
}
