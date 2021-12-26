package com.spring.martadmin.product.service;

import com.spring.martadmin.product.domain.Product;
import com.spring.martadmin.product.dto.ProductResponseDto;
import com.spring.martadmin.product.dto.ProductSaveRequestDto;
import com.spring.martadmin.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    //상품 등록하기
    @Transactional
    public ProductResponseDto saveProduct(ProductSaveRequestDto requestDto) throws Exception {
        Product product = null;

        UUID uuid = UUID.randomUUID();

        // 파일 저장 경로 : product/img/{productNo}
        String dirName = uuid+ "_" + requestDto.getNo();
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        File saveFile = new File(filePath, dirName);
        try {
            product = Product.createProduct(requestDto.getNo(), requestDto.getName(), requestDto.getPrice());
            productRepository.save(product);
            requestDto.getImg().transferTo(saveFile);
        } catch (IOException e) {
            productRepository.delete(product);
            throw new MultipartException("이미지 업로드에 실패하였습니다.");
        }

        product.setImgUrl(dirName);
        productRepository.save(product);
        return ProductResponseDto.of(product);
    }
}
