package com.spring.martadmin.product.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.product.dto.ProductDetailDeleteRequestDto;
import com.spring.martadmin.market.repository.MarketRepository;
import com.spring.martadmin.product.domain.Product;
import com.spring.martadmin.product.domain.ProductDetail;
import com.spring.martadmin.product.dto.ProductDetailResponseDto;
import com.spring.martadmin.product.dto.ProductDetailSaveRequestDto;
import com.spring.martadmin.product.dto.ProductDetailUpdateRequestDto;
import com.spring.martadmin.product.repository.ProductDetailRepository;
import com.spring.martadmin.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailService {

    private final ProductDetailRepository productDetailRepository;
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;

    //마켓에 상품 추가하기
    @Transactional
    public ProductDetailResponseDto saveProductDetail(ProductDetailSaveRequestDto requestDto, int adminNo) throws Exception {

        Market market = marketRepository.findByNo(requestDto.getMarketNO())
                .orElseThrow(() -> new NotFoundDataException("해당 마켓을 찾을 수 없습니다."));

        Product product = productRepository.findByNo(requestDto.getProductNo())
                .orElseThrow(() -> new NotFoundDataException("해당 상품을 찾울 수 없습니다."));

        if(adminNo != market.getAdmin().getNo()) {
            throw new SessionUnstableException("해당 마켓에 접근할수 없습니다.");
        }

        ProductDetail productDetail = ProductDetail.createProductDetail(requestDto.getOnSale(), requestDto.getOnSalePrice(), market, product);
        productDetailRepository.save(productDetail);

        return ProductDetailResponseDto.of(productDetail);
    }

    //마켓에 상품 수정하기
    @Transactional
    public ProductDetailResponseDto updateProductDetail(ProductDetailUpdateRequestDto requestDto, int adminNo) throws Exception {

        ProductDetail productDetail = productDetailRepository.findByNo(requestDto.getProductDetailNo())
                .orElseThrow(() -> new NotFoundDataException("해당 삼품을 찾을 수 없습니다."));

        Market market = marketRepository.findByNo(requestDto.getMarketNO())
                .orElseThrow(() -> new NotFoundDataException("해당 마켓을 찾을 수 없습니다."));

        Product product = productRepository.findByNo(requestDto.getProductNo())
                .orElseThrow(() -> new NotFoundDataException("해당 상품을 찾울 수 없습니다."));

        if(adminNo != market.getAdmin().getNo()) {
            throw new SessionUnstableException("해당 마켓에 접근할수 없습니다.");
        }

        productDetail = productDetail.updateProductDetail(requestDto.getOnSale(), requestDto.getOnSalePrice());
        productDetailRepository.save(productDetail);

        return ProductDetailResponseDto.of(productDetail);
    }

    @Transactional
    public ProductDetailResponseDto deleteProductDetail(ProductDetailDeleteRequestDto requestDto, int adminNo) throws Exception {


        Market market = marketRepository.findByNo(requestDto.getMarketNo())
                .orElseThrow(() -> new NotFoundDataException("해당 마켓을 찾을 수 없습니다."));

        if(adminNo != market.getAdmin().getNo()) {
            throw new SessionUnstableException("해당 마켓에 접근할수 없습니다.");
        }

        ProductDetail productDetail = productDetailRepository.findByNo(requestDto.getProductDetailNo())
                .orElseThrow(() -> new NotFoundDataException("해당 상품을 찾을 수 없습니다."));

        productDetailRepository.delete(productDetail);

        return ProductDetailResponseDto.of(productDetail);
    }
}
