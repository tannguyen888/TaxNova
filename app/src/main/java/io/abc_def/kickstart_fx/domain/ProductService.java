package io.abc_def.kickstart_fx.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductService {
    private final io.abc_def.kickstart_fx.persistence.ProductRepository productRepository;

    public ProductService(io.abc_def.kickstart_fx.persistence.ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ObservableList<Product> getAllProducts() {
        return FXCollections.observableArrayList(productRepository.findAll());
    }

    public ObservableList<Product> getProductsByCategory(String category) {
        return FXCollections.observableArrayList(productRepository.findByCategory(category));
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product getProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.delete(productId);
    }

    public void updateProductQuantity(Long productId, long quantity) {
        productRepository.updateQuantity(productId, quantity);
    }

    public boolean checkAvailability(Long productId, long quantity) {
        Product product = getProductById(productId);
        return product != null && product.getQuantity() >= quantity;
    }
}
