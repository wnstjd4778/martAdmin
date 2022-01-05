package com.spring.martadmin.product.repository;

import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.product.domain.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

    public Optional<ProductDetail> findByNo(int productDetailNo);

    public List<ProductDetail> findByMarket(Market market);

}
