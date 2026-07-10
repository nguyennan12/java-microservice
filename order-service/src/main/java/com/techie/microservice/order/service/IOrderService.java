package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;

public interface IOrderService {
    public void placeOrder(OrderRequest orderRequest);
}
