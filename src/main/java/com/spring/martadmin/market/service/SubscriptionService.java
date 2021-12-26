package com.spring.martadmin.market.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.market.domain.Subscription;
import com.spring.martadmin.market.repository.MarketRepository;
import com.spring.martadmin.market.repository.SubscriptionRepository;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;

    //유저가 마켓을 구독한다.
    @Transactional
    public void subscribeMarket(Integer userNo, Integer marketNo) throws NotFoundDataException {
        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new NotFoundDataException("해당 유저를 찾을 수 없습니다."));

        Market market = marketRepository.findByNo(marketNo)
                .orElseThrow(() -> new NotFoundDataException("해당 마켓를 찾을 수 업습니다."));

        Optional<Subscription> subscription = subscriptionRepository.findByMarketAndUser(market, user);
        if(!subscription.isPresent()) {
            subscriptionRepository.save(Subscription.createSubscription(market, user));
        }
        return;
    }

    //유저가 마켓을 구독을 취소한다.
    @Transactional
    public void unSubscribeMarket(Integer userNo, Integer marketNo) throws NotFoundDataException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new NotFoundDataException("해당 유저를 찾을 수 없습니다."));

        Market market = marketRepository.findByNo(marketNo)
                .orElseThrow(() -> new NotFoundDataException("해당 마켓를 찾을 수 업습니다."));

        log.info("통과");
        subscriptionRepository.findByMarketAndUser(market, user)
                .orElseThrow(() -> new NotFoundDataException("해당 마켓에 구독을 하지 않으셨습니다"));

        subscriptionRepository.deleteByMarketAndUser(market, user);
        return;
    }
}
