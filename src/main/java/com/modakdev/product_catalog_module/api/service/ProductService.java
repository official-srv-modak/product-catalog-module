package com.modakdev.product_catalog_module.api.service;


import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.SingleProductResponse;

public interface ProductService {
    public SingleProductResponse getProduct(int id);
}
