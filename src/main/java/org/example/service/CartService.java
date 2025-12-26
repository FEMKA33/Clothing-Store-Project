package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.CartItem;
import org.example.model.Product;
import org.example.model.UserEntity;
import org.example.repository.CartItemRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public void addToCart(String username, Long productId) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);
    }

    public List<CartItem> getCart(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        return cartItemRepository.findByUser(user);
    }

    public void checkout(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста!");
        }

        cartItemRepository.deleteAll(cartItems);
    }

    public void finalizeCheckout(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        var items = cartItemRepository.findByUser(user);
        if (items.isEmpty()) throw new IllegalStateException("Корзина пуста!");

        cartItemRepository.deleteAll(items);
    }

    public void clearCart(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        cartItemRepository.deleteAll(cartItemRepository.findByUser(user));
    }
}