package com.techie.microservice.order.service;

import com.techie.microservice.order.client.InventoryClient;
import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.event.PlaceOrderEvent;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import com.techie.microservice.order.model.Order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.techie.microservice.order.repository.OrderRepository;

import java.util.UUID;

@lombok.extern.slf4j.Slf4j
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService{
    private  final OrderRepository orderRepository;
    private final InventoryClient iventoryClient;
    private final KafkaTemplate<String, PlaceOrderEvent> kafkaTemplate;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
//        var isProductInStock = iventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
//        if(isProductInStock){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
            //send message kafka
            PlaceOrderEvent placeOrderEvent = new PlaceOrderEvent(order.getOrderNumber(), orderRequest.userDetails().email());
            log.info("Star - Sending order placed event to kafka for order number: {}", order.getOrderNumber());
            kafkaTemplate.send("order-placed", placeOrderEvent);
            log.info("End - Order placed event sent to kafka for order number: {}", order.getOrderNumber());
//        }
//        else{
//            throw new RuntimeException("Product " + orderRequest.skuCode() + " is not in stock, please try again later");
//        }

    }
}
