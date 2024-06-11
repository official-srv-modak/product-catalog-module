package com.modakdev.product_catalog_module.api.service.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.modakdev.lib.LibraryFunctions;
import com.modakdev.model.pojo.Product;
import com.modakdev.product_catalog_module.api.client.GenericModelTrainerClient;
import com.modakdev.product_catalog_module.api.service.ProductService;
import com.modakdev.product_catalog_module.config.ServerIpConfig;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    GenericModelTrainerClient client;

    private final ServerIpConfig serverIpConfig;

    @Value("${flask.img.base-url}")
    String flaskImgBaseUrl;

    @Autowired
    public ProductServiceImpl(GenericModelTrainerClient client, ServerIpConfig serverIpConfig) {
        this.client = client;
        this.serverIpConfig = serverIpConfig;
    }

    @Override
    public SingleProductResponse getProduct(int id) {

        SingleProductResponse  baseResponse = new SingleProductResponse();
        try{
            Object flaskResponse = client.getSingleProduct(id);
            JSONObject jsonObject = LibraryFunctions.convertToJSONObject((LinkedHashMap<String, Object>)flaskResponse);
            LibraryFunctions.fixLists(jsonObject);
            Gson gson = new Gson();
            Product product = gson.fromJson(jsonObject.toString(), Product.class);
            product.setImageUrl(flaskImgBaseUrl+id);
            baseResponse.setStatus(HttpStatus.OK);
            baseResponse.setMessage("Product found");
            if (product.getDescription() == null || product.getDescription().isEmpty()) {
                product.setDescription(LibraryFunctions.buildDescription(product.getName()));
            }
            product.setAccuracy(LibraryFunctions.getAccuracyPercentage(product.getAccuracy()));
            baseResponse.build(product);
        }
        catch (Exception e) {
            baseResponse.setMessage("Product not found " + e.getMessage());
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            return baseResponse;
        }
    }

    @Override
    public MultipleProductResponse getAllProducts() {
        MultipleProductResponse  baseResponse = new MultipleProductResponse();
        try{
            ArrayList flaskResponse = (ArrayList) client.getAllProducts();
            ArrayList<Product> products = new ArrayList<>();
            Gson gson = new Gson();
            for(Object map : flaskResponse)
            {
                JSONObject jsonObject = LibraryFunctions.convertToJSONObject((LinkedHashMap<String, Object>)map);
                LibraryFunctions.fixLists(jsonObject);
                Product product = gson.fromJson(jsonObject.toString(), Product.class);
                if (product.getDescription() == null || product.getDescription().isEmpty()) {
                    product.setDescription(LibraryFunctions.buildDescription(product.getName()));
                }
                product.setImageUrl(flaskImgBaseUrl+product.getId());
                product.setAccuracy(LibraryFunctions.getAccuracyPercentage(product.getAccuracy()));
                products.add(product);
            }
            baseResponse.build(products);
            baseResponse.setStatus(HttpStatus.OK);
            baseResponse.setMessage("Products found");
        }
        catch (Exception e) {
            baseResponse.setMessage("Failed to request " + e.getMessage());
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            return baseResponse;
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> getCorrelationPlotImg(String modelName, String modelPath) {
        try {
            return client.callCorrelationImgPlotApi(modelName, modelPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
