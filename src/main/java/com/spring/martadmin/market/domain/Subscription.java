package com.spring.martadmin.market.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer no;

    @ManyToOne
    @JoinColumn(name = "market_no")
    private Market market;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;

    public static Subscription createSubscription(Market market, User user) {
        Subscription subscription = Subscription.builder()
                                        .market(market)
                                        .user(user)
                                        .build();

        return subscription;
    }
}
