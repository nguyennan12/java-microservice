package com.techie.microservices.product.service;

import com.techie.microservices.product.dto.ProductRequest;
import com.techie.microservices.product.dto.ProductResponse;
import com.techie.microservices.product.model.Product;

import java.util.List;


public interface IProductService {
    public ProductResponse createProduct(ProductRequest productRequest);
    public List<ProductResponse> getAllProducts();
}
