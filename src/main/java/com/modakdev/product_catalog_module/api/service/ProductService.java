package com.modakdev.product_catalog_module.api.service;


import com.modakdev.request.TrainingMultipartRequest;
import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;
import com.modakdev.response.TrainModelResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    public SingleProductResponse getProduct(int id);
    public MultipleProductResponse getAllProducts();
    public ResponseEntity<InputStreamResource> getCorrelationPlotImg(String modelName, String modelPath);
    public TrainModelResponse trainModel(TrainingMultipartRequest request);
    public MDBaseResponse uploadFiles(MultipartFile trainFile, MultipartFile testFile);
}
