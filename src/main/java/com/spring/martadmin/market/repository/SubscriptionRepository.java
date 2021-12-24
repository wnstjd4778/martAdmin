package com.spring.martadmin.market.repository;

import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.market.domain.Subscription;
import com.spring.martadmin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    public void deleteByMarketAndUser(Market market, User user);

    public Optional<Subscription> findByMarketAndUser(Market market, User user);
}
