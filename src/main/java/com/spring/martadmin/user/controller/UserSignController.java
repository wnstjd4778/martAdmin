package com.spring.martadmin.user.controller;

import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.user.domain.AuthProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.dto.AuthResponseDto;
import com.spring.martadmin.user.dto.UserSignInRequestDto;
import com.spring.martadmin.user.dto.UserSignUpRequestDto;
import com.spring.martadmin.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserSignController {

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @ApiOperation(value = "로컬 로그인", notes = "로컬 회원 로그인을 시도한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 로그인되었습니다.", response = AuthResponseDto.class),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 401, message = "아이디 또는 비밀번호가 일치하지 않습니다."),
            @ApiResponse(code = 428, message = "비밀번호를 변경해야 합니다.", response = AuthResponseDto.class)
    })
    @PostMapping("/users/signin")
    public ResponseEntity<AuthResponseDto> signIn(@ApiParam("로그인 정보") @Valid @RequestBody UserSignInRequestDto requestDto) throws SessionUnstableException {
        User user = userService.checkLogIn(requestDto.getEmail(), requestDto.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(),
                requestDto.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken); //loadUserByname 호출
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        /* access 토큰과 refresh 토큰을 발급 */
        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);
        log.info(accessToken);
        log.info(refreshToken);

        Date expirationDate = jwtTokenProvider.getExpirationDate(refreshToken, JwtTokenProvider.TokenType.REFRESH_TOKEN);

        /* 임시 비밀번호 여부 체크 */
        if(user.isTempPassword()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(new AuthResponseDto(accessToken));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDto(accessToken));
    }

    @ApiOperation(value = "로컬 회원 가입", notes = "로컬 회원 가입을 한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 회원가입이 되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "이메일 또는 핸드폰 번호가 중복되었습니다. ")
    })
    @PostMapping("/users/signup")
    public ResponseEntity<Void> signUp(@ApiParam("회원가입 정보") @Valid @RequestBody UserSignUpRequestDto requestDto) throws DuplicateDataException {
        userService.checkDuplicateEmail(requestDto.getEmail());
        userService.checkDuplicateTel(requestDto.getTel());

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .tel(requestDto.getTel())
                .name(requestDto.getName())
                .snsType(AuthProvider.local)
                .build();
        userService.saveUser(user);
        log.info(requestDto.getName() + "님이 회원가입 하였습니다");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
