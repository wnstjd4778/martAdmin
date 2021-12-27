package com.spring.martadmin.cart.repository;

import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByNo(int cartNo);

    Optional<Cart> findByUser(User user);
}
