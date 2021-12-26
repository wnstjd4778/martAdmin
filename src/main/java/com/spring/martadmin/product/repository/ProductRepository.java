package com.spring.martadmin.product.repository;

import com.spring.martadmin.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByNo(int productNo);
}
