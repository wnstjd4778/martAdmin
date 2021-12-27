package com.spring.martadmin.cart.repository;

import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.domain.CartItem;
import com.spring.martadmin.product.domain.ProductDetail;
import com.spring.martadmin.product.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByNo(int cartItemNo);

    Optional<CartItem> findByCartAndProductDetail(Cart cart, ProductDetail productDetail);

    List<CartItem> findAllByCart(Cart cart);
}
