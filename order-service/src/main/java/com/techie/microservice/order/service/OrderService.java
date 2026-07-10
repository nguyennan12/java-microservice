package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import com.techie.microservice.order.model.Order;
import org.springframework.stereotype.Service;
import com.techie.microservice.order.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private  final OrderRepository orderRepository;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());

        orderRepository.save(order);
    }
}
