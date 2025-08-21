package org.motive.service;

import org.motive.entity.Product;
import org.motive.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    // private final RestTemplate restTemplate;
    // private final ObjectMapper objectMapper;
    // private static final String THIRD_PARTY_API_URL = "https://api.thirdparty.com/products";

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        // this.restTemplate = new RestTemplate();
        // this.objectMapper = new ObjectMapper();
    }

    public Product createProduct(Product product) {
        if (product.getId() == null || product.getId().trim().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(String id, Product productDetails) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> importProductsFromCsv(String csvFilePath) throws IOException {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 4) {
                    Product product = new Product();
                    product.setId(UUID.randomUUID().toString());
                    product.setName(values[0].trim());
                    product.setDescription(values[1].trim());
                    product.setPrice(new BigDecimal(values[2].trim()));
                    product.setQuantity(Integer.valueOf(values[3].trim()));
                    
                    Product savedProduct = productRepository.save(product);
                    products.add(savedProduct);
                }
            }
        }
        
        // Sync imported products to 3rd party API
        // syncProductsToThirdPartyAPI(products);
        
        return products;
    }

    // /**
    //  * Syncs product data to a 3rd party API
    //  * This method would be called after CSV import to keep external systems in sync
    //  */
    // private void syncProductsToThirdPartyAPI(List<Product> products) {
    //     try {
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);
    //         headers.setBearerAuth("your-api-token-here"); // Replace with actual token
    //         
    //         for (Product product : products) {
    //             // Create payload for 3rd party API
    //             ThirdPartyProductDTO thirdPartyProduct = new ThirdPartyProductDTO();
    //             thirdPartyProduct.setExternalId(product.getId());
    //             thirdPartyProduct.setProductName(product.getName());
    //             thirdPartyProduct.setProductDescription(product.getDescription());
    //             thirdPartyProduct.setUnitPrice(product.getPrice());
    //             thirdPartyProduct.setStockQuantity(product.getQuantity());
    //             thirdPartyProduct.setSyncTimestamp(System.currentTimeMillis());
    //             
    //             String jsonPayload = objectMapper.writeValueAsString(thirdPartyProduct);
    //             HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
    //             
    //             // Make API call to sync product
    //             ResponseEntity<String> response = restTemplate.exchange(
    //                 THIRD_PARTY_API_URL + "/sync",
    //                 HttpMethod.POST,
    //                 request,
    //                 String.class
    //             );
    //             
    //             if (response.getStatusCode().is2xxSuccessful()) {
    //                 System.out.println("Successfully synced product: " + product.getName());
    //             } else {
    //                 System.err.println("Failed to sync product: " + product.getName() + 
    //                                  ", Status: " + response.getStatusCode());
    //             }
    //             
    //             // Add small delay to avoid rate limiting
    //             Thread.sleep(100);
    //         }
    //         
    //     } catch (Exception e) {
    //         System.err.println("Error syncing products to 3rd party API: " + e.getMessage());
    //         // In production, you might want to:
    //         // 1. Log the error properly
    //         // 2. Store failed syncs in a queue for retry
    //         // 3. Send alerts to monitoring systems
    //     }
    // }

    // /**
    //  * Batch sync method for better performance with large datasets
    //  */
    // private void batchSyncProductsToThirdPartyAPI(List<Product> products) {
    //     try {
    //         HttpHeaders headers = new HttpHeaders();
    //         headers.setContentType(MediaType.APPLICATION_JSON);
    //         headers.setBearerAuth("your-api-token-here");
    //         
    //         // Convert products to 3rd party format
    //         List<ThirdPartyProductDTO> thirdPartyProducts = products.stream()
    //             .map(product -> {
    //                 ThirdPartyProductDTO dto = new ThirdPartyProductDTO();
    //                 dto.setExternalId(product.getId());
    //                 dto.setProductName(product.getName());
    //                 dto.setProductDescription(product.getDescription());
    //                 dto.setUnitPrice(product.getPrice());
    //                 dto.setStockQuantity(product.getQuantity());
    //                 dto.setSyncTimestamp(System.currentTimeMillis());
    //                 return dto;
    //             }).toList();
    //         
    //         // Create batch request
    //         BatchSyncRequest batchRequest = new BatchSyncRequest();
    //         batchRequest.setProducts(thirdPartyProducts);
    //         batchRequest.setBatchId(UUID.randomUUID().toString());
    //         batchRequest.setSource("csv-import");
    //         
    //         String jsonPayload = objectMapper.writeValueAsString(batchRequest);
    //         HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
    //         
    //         // Make batch API call
    //         ResponseEntity<BatchSyncResponse> response = restTemplate.exchange(
    //             THIRD_PARTY_API_URL + "/batch-sync",
    //             HttpMethod.POST,
    //             request,
    //             BatchSyncResponse.class
    //         );
    //         
    //         if (response.getStatusCode().is2xxSuccessful()) {
    //             BatchSyncResponse syncResponse = response.getBody();
    //             System.out.println("Batch sync completed. Success: " + syncResponse.getSuccessCount() + 
    //                              ", Failed: " + syncResponse.getFailedCount());
    //         } else {
    //             System.err.println("Batch sync failed with status: " + response.getStatusCode());
    //         }
    //         
    //     } catch (Exception e) {
    //         System.err.println("Error in batch sync to 3rd party API: " + e.getMessage());
    //     }
    // }

    // // DTO classes for 3rd party API communication
    // private static class ThirdPartyProductDTO {
    //     private String externalId;
    //     private String productName;
    //     private String productDescription;
    //     private BigDecimal unitPrice;
    //     private Integer stockQuantity;
    //     private Long syncTimestamp;
    //     
    //     // Getters and setters
    //     public String getExternalId() { return externalId; }
    //     public void setExternalId(String externalId) { this.externalId = externalId; }
    //     public String getProductName() { return productName; }
    //     public void setProductName(String productName) { this.productName = productName; }
    //     public String getProductDescription() { return productDescription; }
    //     public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    //     public BigDecimal getUnitPrice() { return unitPrice; }
    //     public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    //     public Integer getStockQuantity() { return stockQuantity; }
    //     public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    //     public Long getSyncTimestamp() { return syncTimestamp; }
    //     public void setSyncTimestamp(Long syncTimestamp) { this.syncTimestamp = syncTimestamp; }
    // }

    // private static class BatchSyncRequest {
    //     private String batchId;
    //     private String source;
    //     private List<ThirdPartyProductDTO> products;
    //     
    //     // Getters and setters
    //     public String getBatchId() { return batchId; }
    //     public void setBatchId(String batchId) { this.batchId = batchId; }
    //     public String getSource() { return source; }
    //     public void setSource(String source) { this.source = source; }
    //     public List<ThirdPartyProductDTO> getProducts() { return products; }
    //     public void setProducts(List<ThirdPartyProductDTO> products) { this.products = products; }
    // }

    // private static class BatchSyncResponse {
    //     private String batchId;
    //     private int successCount;
    //     private int failedCount;
    //     private List<String> failedProductIds;
    //     
    //     // Getters and setters
    //     public String getBatchId() { return batchId; }
    //     public void setBatchId(String batchId) { this.batchId = batchId; }
    //     public int getSuccessCount() { return successCount; }
    //     public void setSuccessCount(int successCount) { this.successCount = successCount; }
    //     public int getFailedCount() { return failedCount; }
    //     public void setFailedCount(int failedCount) { this.failedCount = failedCount; }
    //     public List<String> getFailedProductIds() { return failedProductIds; }
    //     public void setFailedProductIds(List<String> failedProductIds) { this.failedProductIds = failedProductIds; }
    // }
}