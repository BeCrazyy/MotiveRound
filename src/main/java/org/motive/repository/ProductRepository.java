package org.motive.repository;

import org.motive.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final DynamoDbTable<Product> productTable;

    @Autowired
    public ProductRepository(DynamoDbTable<Product> productTable) {
        this.productTable = productTable;
    }

    public Product save(Product product) {
        productTable.putItem(product);
        return product;
    }

    public Optional<Product> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Product product = productTable.getItem(key);
        return Optional.ofNullable(product);
    }

    public List<Product> findAll() {
        return productTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .toList();
    }

    public void deleteById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        productTable.deleteItem(key);
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public void delete(Product product) {
        productTable.deleteItem(product);
    }

    public long count() {
        return productTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .count();
    }

    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .filter(product -> product.getPrice().compareTo(minPrice) >= 0 && 
                                 product.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }
    
    public List<Product> findByName(String name) {
        return productTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}