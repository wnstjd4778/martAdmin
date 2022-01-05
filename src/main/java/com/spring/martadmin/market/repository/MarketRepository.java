package com.spring.martadmin.market.repository;

import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.user.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Integer> {

    public Optional<Market> findByNo(Integer no);

    public Optional<String> deleteAllByNoAndAdmin(Integer marketNo, Admin admin);


}
