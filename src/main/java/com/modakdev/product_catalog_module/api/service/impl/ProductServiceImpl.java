package com.modakdev.product_catalog_module.api.service.impl;


import com.google.gson.Gson;
import com.modakdev.lib.LibraryFunctions;
import com.modakdev.model.pojo.Product;
import com.modakdev.product_catalog_module.api.client.GenericModelTrainerClient;
import com.modakdev.product_catalog_module.api.service.ProductService;
import com.modakdev.response.SingleProductResponse;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class ProductServiceImpl implements ProductService {

    GenericModelTrainerClient client;

    @Autowired
    public ProductServiceImpl(GenericModelTrainerClient client) {
        this.client = client;
    }

    @Override
    public SingleProductResponse getProduct(int id) {
        SingleProductResponse  baseResponse = new SingleProductResponse();
        try{
            Object flaskResponse = client.getSingleQueryResponse(id);
            JSONObject jsonObject = LibraryFunctions.convertToJSONObject((LinkedHashMap<String, Object>)flaskResponse);
            Gson gson = new Gson();
            Product product = gson.fromJson(jsonObject.toJSONString(), Product.class);
            baseResponse.build(product);
        }
        catch (Exception e) {
            baseResponse.setMessage("Failed to request " + e.getMessage());
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            return baseResponse;
        }
    }
}
