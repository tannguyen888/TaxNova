package io.abc_def.kickstart_fx.domain;

import io.abc_def.kickstart_fx.persistence.CartRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findActiveCartByUserId(userId);
        if (cart == null) {
            cart = new Cart(userId);
            cartRepository.save(cart);
        }
        return cart;
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    public void addItemToCart(Cart cart, CartItem item) {
        item.setCartId(cart.getId());
        cart.addItem(item);
        cartRepository.saveCartItem(item);
        cartRepository.updateCartStatus(cart);
    }

    public void removeItemFromCart(Cart cart, Long itemId) {
        cartRepository.deleteCartItem(itemId);
        cart.removeItemById(itemId);
        cartRepository.updateCartStatus(cart);
    }

    public void updateCartItem(CartItem item) {
        cartRepository.saveCartItem(item);
    }

    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cartRepository.clearCart(cart.getId());
        cart.clearItems();
    }

    public void deleteCart(Long cartId) {
        cartRepository.delete(cartId);
    }

    public ObservableList<Cart> getCompletedCarts() {
        return FXCollections.observableArrayList(cartRepository.findByStatus("COMPLETED"));
    }

    public double calculateTotal(Cart cart) {
        return cart.getTotalPrice();
    }

    public double calculateTax(Cart cart, double taxRate) {
        return calculateTotal(cart) * taxRate;
    }

    public double calculateGrandTotal(Cart cart, double taxRate) {
        return calculateTotal(cart) + calculateTax(cart, taxRate);
    }
}
