package com.spring.martadmin.order.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.domain.CartItem;
import com.spring.martadmin.cart.repository.CartRepository;
import com.spring.martadmin.cart.service.CartService;
import com.spring.martadmin.order.domain.Order;
import com.spring.martadmin.order.domain.OrderDetail;
import com.spring.martadmin.order.dto.OrderRequestDto;
import com.spring.martadmin.order.dto.OrderResponseDto;
import com.spring.martadmin.order.repository.OrderDetailRepository;
import com.spring.martadmin.order.repository.OrderRepository;
import com.spring.martadmin.product.domain.ProductDetail;
import com.spring.martadmin.product.repository.ProductDetailRepository;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    // 주문하기
    @Transactional
    public OrderResponseDto order(int userNo, OrderRequestDto requestDto) throws Exception {
        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 업습니다."));

        log.info("{}  {}", userNo, requestDto.getCartNo());
        Cart cart = cartRepository.findByNo(requestDto.getCartNo())
                .orElseThrow(() -> new NullPointerException("장바구니를 불러오는데 실패하였습니다."));

        // 주문 상품정보를 생성
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItems()) {
            OrderDetail orderDetail = OrderDetail.createOrderDetail(cartItem.getProductDetail(), cartItem.getCount());
            orderDetails.add(orderDetail);
        }

        Order order = Order.createOrder(user, orderDetails);
        orderRepository.save(order);
        orderDetails.stream().forEach(orderDetail -> orderDetail.setOrder(order));
        orderDetailRepository.saveAll(orderDetails);

        cartService.removeAllCartItem(user, cart);

        return OrderResponseDto.of(order);
    }

    // 주문 취소(Admin)
    @Transactional
    public void cancelAll(int orderNo) {
        Order order = orderRepository.findByNo(orderNo)
                .orElseThrow(() -> new NotFoundDataException("해당 주문을 찾을 수 없습니다."));

        order.cancel();
    }
}
