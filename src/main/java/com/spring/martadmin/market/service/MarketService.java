package com.spring.martadmin.market.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.market.dto.MarketResponseDto;
import com.spring.martadmin.market.dto.MarketSaveRequestDto;
import com.spring.martadmin.market.dto.MarketUpdateRequestDto;
import com.spring.martadmin.market.repository.MarketRepository;
import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.AdminRepository;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    //마켓 등록하기
    @Transactional
    public MarketResponseDto saveMarket(MarketSaveRequestDto requestDto) throws Exception {
        Market market = null;

        Admin admin = adminRepository.findByNo(requestDto.getAdminNo())
                .orElseThrow(() -> new NotFoundDataException("해당 관리자가 존재하지 않습니다."));

        User user = userRepository.findByNo(1)
                .orElseThrow(() -> new Exception("확인용"));
        market = Market.createMarket(admin, user, requestDto.getName(), requestDto.getTel(), requestDto.getOpenTime(), requestDto.getCloseTime(), requestDto.getLocation());
        marketRepository.save(market);

        return MarketResponseDto.of(market);
    }

    //마켓 수정하기
    @Transactional
    public MarketResponseDto updateMarket(MarketUpdateRequestDto requestDto) throws Exception {

        Market market = marketRepository.findByNo(requestDto.getMarketNo())
                .orElseThrow(() -> new NotFoundDataException("마켓이 존재하지 않습니다."));
        if(market.getAdmin().getNo() != requestDto.getAdminNo()) {
            throw new NotFoundDataException("해당 마켓을 수정할 수 없습니다.");
        }
        market = market.update(requestDto.getName(), requestDto.getTel(), requestDto.getOpenTime(), requestDto.getCloseTime(), requestDto.getLocation());

        marketRepository.save(market);
        return MarketResponseDto.of(market);
    }

    //상품 제거하기
    @Transactional
    public void removeMarket(Integer adminNo, Integer marketNo) throws NotFoundDataException, SessionUnstableException {
        Market market = marketRepository.findByNo(marketNo)
                .orElseThrow(() -> new NotFoundDataException("마켓이 존재하지 않습니다."));

        Admin admin = adminRepository.findByNo(adminNo)
                .orElseThrow(() -> new NotFoundDataException("해당 관리자가 존재하지 않습니다."));

        if(market.getAdmin().getNo() != adminNo) {
            throw new SessionUnstableException("해당 마켓을 삭제할 수 없습니다.");
        }
        marketRepository.deleteAllByNoAndAdmin(marketNo, admin);
    }

}
