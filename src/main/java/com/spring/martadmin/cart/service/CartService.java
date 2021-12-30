package com.spring.martadmin.cart.service;


import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.domain.CartItem;
import com.spring.martadmin.cart.dto.CartResponseDto;
import com.spring.martadmin.cart.repository.CartItemRepository;
import com.spring.martadmin.cart.repository.CartRepository;
import com.spring.martadmin.product.domain.ProductDetail;
import com.spring.martadmin.product.repository.ProductDetailRepository;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductDetailRepository productDetailRepository;

    //장바구니에 상품 추가
    public void addProductInCart(int userNo, int productDetailNo) throws SessionUnstableException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니다."));

        ProductDetail productDetail = productDetailRepository.findByNo(productDetailNo)
                .orElseThrow(() -> new NotFoundDataException("해당 제품이 존재하지 않습니다."));

        Optional<Cart> cart = cartRepository.findByUser(user);

        if(cart.isPresent()) {
            Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductDetail(cart.get(), productDetail);
            if(cartItem.isPresent()) {
                cartItem.get().addCartItem();
                cartItemRepository.save(cartItem.get());
            } else {
                cartItemRepository.save(CartItem.createCartItem(cart.get(), productDetail));
            }
        } else {
            Cart newCart = Cart.createCart(user);
            cartRepository.save(newCart);

            cartItemRepository.save(CartItem.createCartItem(newCart, productDetail));
        }
    }

    //카트에 담긴 상품 갯수 1개 증가
    public void addProduct(int userNo, int productDetailNo) throws SessionUnstableException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니다."));

        ProductDetail productDetail = productDetailRepository.findByNo(productDetailNo)
                .orElseThrow(() -> new NotFoundDataException("해당 제품이 존재하지 않습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundDataException("해당 유저의 장바구니를 찾을 수 없습니다."));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductDetail(cart, productDetail);

        if(cartItem.isPresent()) {
            cartItem.get().addCartItem();
            cartItemRepository.save(cartItem.get());
        }
    }

    //카트에 담긴 상품 갯수 1개 감소
    public void subProduct(int userNo, int productDetailNo) throws SessionUnstableException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니다."));

        ProductDetail productDetail = productDetailRepository.findByNo(productDetailNo)
                .orElseThrow(() -> new NotFoundDataException("해당 제품이 존재하지 않습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundDataException("해당 유저의 장바구니를 찾을 수 없습니다."));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductDetail(cart, productDetail);

        if(cartItem.isPresent()) {
            cartItem.get().subCartItem();
            if(cartItem.get().getCount() == 0) {
                cartItemRepository.delete(cartItem.get());
            } else {
                cartItemRepository.save(cartItem.get());
            }
        }
    }

    // 해당 사용자 장바구니 목록 조회하기
    public CartResponseDto showUserCart(int userNo) throws SessionUnstableException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundDataException("해당 유저의 장바구니를 찾을 수 업습니다."));

        log.info("{}", cart.getNo());
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);
        log.info("{}", cartItemRepository.findAllByCart(cart));
        return CartResponseDto.of(cartItems, cart);
    }

    //카트에서 상품 제거
    public void takeProductOutOfCart(int userNo, int productDetailNo) throws SessionUnstableException {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니다."));

        ProductDetail productDetail = productDetailRepository.findByNo(productDetailNo)
                .orElseThrow(() -> new NotFoundDataException("해당 상품이 존재하지 않습니다."));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundDataException("해당 유저의 장바구니를 찾을 수 없습니다."));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductDetail(cart, productDetail);

        if(cartItem.isPresent()) {
            cartItem.get().removeCartItem();
            cartItemRepository.delete(cartItem.get());
        }
    }

    // 카트 전체 삭제
    public void removeAllCartItem(User user, Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

        // 카트에 담겨있는 카트 아이템 모두 삭제
        for(CartItem cartItem : cartItems) {
            cartItem.removeCartItem();
            cartItemRepository.delete(cartItem);
        }

        cartRepository.delete(cart);
    }

}
