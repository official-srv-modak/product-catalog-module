package com.modakdev.product_catalog_module.api.service;


import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface ProductService {
    public SingleProductResponse getProduct(int id);
    public MultipleProductResponse getAllProducts();
    public ResponseEntity<InputStreamResource> getCorrelationPlotImg(String modelName, String modelPath);
}
