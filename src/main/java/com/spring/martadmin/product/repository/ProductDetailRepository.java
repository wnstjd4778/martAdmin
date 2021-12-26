package com.spring.martadmin.product.repository;

import com.spring.martadmin.product.domain.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

    public Optional<ProductDetail> findByNo(int productDetailNo);

}
