package com.modakdev.product_catalog_module.api.controller;

import com.modakdev.product_catalog_module.api.service.impl.ProductServiceImpl;
import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${product.controller.base.path}")
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

    @GetMapping("/get-all-products")
    public MultipleProductResponse getAllProducts() {
        MultipleProductResponse response = productService.getAllProducts();
        return response;
    }
    @GetMapping("${correlation.matrix.img-url}")
    public Object getCorrelationPlotImg(@PathVariable int id){
        SingleProductResponse response = productService.getProduct(id);
        if(response.getProduct()!=null)
        {
            String modelName = response.getProduct().getName();
            String trainsetPath = response.getProduct().getTrainModelPath();
            ResponseEntity<InputStreamResource> resourceResponseEntity = productService.getCorrelationPlotImg(modelName, trainsetPath);
            return resourceResponseEntity;
        }
        else{
            MDBaseResponse baseResponse = new MDBaseResponse();
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("product with id : "+id+" is not found");
            return baseResponse;
        }
    }
}
