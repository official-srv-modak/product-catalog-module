package com.modakdev.product_catalog_module.api.controller;

import com.modakdev.product_catalog_module.api.service.impl.ProductServiceImpl;
import com.modakdev.request.TrainingMultipartRequest;
import com.modakdev.response.MDBaseResponse;
import com.modakdev.response.MultipleProductResponse;
import com.modakdev.response.SingleProductResponse;

import com.modakdev.response.TrainModelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@RestController
@RequestMapping("${product.controller.base.path}")
public class ProductController {

    private final ProductServiceImpl productService;

    private final String TRAIN_UPLOAD_DIR, TEST_UPLOAD_DIR;


    @Autowired
    public ProductController(ProductServiceImpl productService, @Value("${commons.trainset.file-path}")String TRAIN_UPLOAD_DIR, @Value("${commons.testset.file-path}")String TEST_UPLOAD_DIR) {
        this.productService = productService;
        this.TRAIN_UPLOAD_DIR = TRAIN_UPLOAD_DIR;
        this.TEST_UPLOAD_DIR = TEST_UPLOAD_DIR;
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
/*    @GetMapping("${correlation.matrix.img-url}")
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
    }*/

    @PostMapping("/upload-files")
    public MDBaseResponse uploadFiles(@RequestParam("trainFile") MultipartFile trainFile, @RequestParam("testFile") MultipartFile testFile) {
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
