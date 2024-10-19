package com.modakdev.product_catalog_module.api.service.impl;


import com.google.gson.Gson;
import com.modakdev.lib.LibraryFunctions;
import com.modakdev.model.pojo.Product;
import com.modakdev.product_catalog_module.api.client.GenericModelTrainerClient;
import com.modakdev.product_catalog_module.api.service.ProductService;
import com.modakdev.request.TrainingRequest;
import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    GenericModelTrainerClient client;

    @Value("${commons.trainset.file-path}")
    String TRAIN_UPLOAD_DIR;

    @Value("${commons.testset.file-path}")
    String TEST_UPLOAD_DIR;

    @Value("${flask.img.base-url}")
    String flaskImgBaseUrl;

    @Autowired
    public ProductServiceImpl(GenericModelTrainerClient client) {
        this.client = client;
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

    @Override
    public Object trainModel(TrainingRequest request) {
        Object response = client.trainModel(request);

        JSONObject jsonObject = LibraryFunctions.convertToJSONObject((LinkedHashMap<String, Object>)response);
        LibraryFunctions.fixLists(jsonObject);

        return jsonObject;
    }

    @Override
    public MDBaseResponse uploadFiles(MultipartFile trainFile, MultipartFile testFile) {
        if (testFile.isEmpty() || trainFile.isEmpty()) {
            return new MDBaseResponse(HttpStatus.BAD_REQUEST, "Please select a file!");
        } else {
            try {
                // Ensure the upload directory exists
                uploadTransfers(trainFile, TRAIN_UPLOAD_DIR);
                uploadTransfers(testFile, TEST_UPLOAD_DIR);

                return new MDBaseResponse(HttpStatus.OK, "Files uploaded successfully");
            } catch (IOException e) {
                return new MDBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
            }
        }
    }

    private static void uploadTransfers(MultipartFile trainFile, String UPLOAD_DIR) throws IOException {
        Path path = Paths.get(UPLOAD_DIR);
        File uploadDir = path.toFile();

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // Save the file locally
        String trainFilePath = UPLOAD_DIR + trainFile.getOriginalFilename();
        File dest = new File(trainFilePath);
        trainFile.transferTo(dest.toPath());
    }
}
