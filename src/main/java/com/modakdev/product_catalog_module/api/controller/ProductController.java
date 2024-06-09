package com.modakdev.product_catalog_module.api.controller;

import com.modakdev.product_catalog_module.api.service.impl.ProductServiceImpl;
import com.modakdev.response.SingleProductResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductServiceImpl productService;



    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @GetMapping("/get-product/{id}")
    public SingleProductResponse getProduct(@PathVariable int id) {
        SingleProductResponse response = productService.getProduct(id);
        return response;
    }
}
