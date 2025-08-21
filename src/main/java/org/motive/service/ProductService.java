package org.motive.service;

import org.motive.entity.Product;
import org.motive.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}