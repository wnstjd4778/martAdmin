package com.spring.martadmin.user.controller;

import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.SMSException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.domain.AuthProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.dto.*;
import com.spring.martadmin.user.service.AdminService;
import com.spring.martadmin.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserSignController {

    private final UserService userService;

    private final AdminService adminService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @ApiOperation(value = "로컬 로그인", notes = "로컬 회원 로그인을 시도한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 로그인되었습니다.", response = AuthResponseDto.class),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 401, message = "아이디 또는 비밀번호가 일치하지 않습니다."),
            @ApiResponse(code = 428, message = "비밀번호를 변경해야 합니다.", response = AuthResponseDto.class)
    })
    @PostMapping("/user/signin")
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
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal); // 추후 redis 서버에 넣음


        /* 임시 비밀번호 여부 체크 */
        if (user.isTempPassword()) {
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
    @PostMapping("/user/signup")
    public ResponseEntity<Void> signUp(@ApiParam("회원가입 정보") @Valid @RequestBody UserSignUpRequestDto requestDto) throws DuplicateDataException {
        userService.checkDuplicateEmail(requestDto.getEmail());
        userService.checkDuplicateTel(requestDto.getTel());
        log.info("통과");
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

    @ApiOperation(value = "관리자 로그인", notes = "관리자 로그인을 시도한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 로그인을 시도한다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 401, message = "아이디 또는 비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/admin/signin")
    public ResponseEntity<AuthResponseDto> adminSignIn(@ApiParam("로그인 정보") @Valid @RequestBody AdminSignInRequestDto requestDto) throws SessionUnstableException {

        adminService.checkLogin(requestDto.getId(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getId(),
                        requestDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal); // 추후 redis서버에 넣음

        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDto(accessToken));
    }

    @ApiOperation(value = "관리자 회원가입", notes = "관리자가 회원가입을 한다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 회원가입이 완료되었습니다."),
            @ApiResponse(code = 400, message = "유효한 아이디가 아닙니다."),
            @ApiResponse(code = 403, message = "아이디가 중복되어 회원가입에 실패하였습니다.")
    })
    @PostMapping("/admin/signup")
    public ResponseEntity<Void> adminSignUp(@ApiParam("로그인 정보") @Valid @RequestBody AdminSignUpRequestDto requestDto) throws DuplicateDataException {

        adminService.checkDuplicateId(requestDto.getId());

        Admin admin = Admin.builder()
                .id(requestDto.getId())
                .password(requestDto.getPassword())
                .build();

        adminService.saveAdmin(admin);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "회원가입할 때 핸드폰 본인인증", notes = "회원가입할 때 핸드폰 본인인증 메세지를 전송한다. ")
    @ApiResponses({
            @ApiResponse(code = 200, message = "전송된 인증번호 반환"),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "동일한 휴대폰 번호의 회원이 존재합니다."),
            @ApiResponse(code = 500, message = "메세지 전송 실패")
    })
    @PostMapping("/user/signup/message")
    public ResponseEntity<Integer> validPhoneForSignUp(@ApiParam("휴대폰 번호") @Valid @RequestBody SendMessageRequestDto requestDto) throws SMSException, DuplicateDataException {
        userService.checkDuplicateTel(requestDto.getPhoneNo());
        int validNum = userService.validatePhone(requestDto.getPhoneNo());
        return ResponseEntity.status(HttpStatus.OK).body(validNum);
    }

    @ApiOperation(value = "이메일 중복 검사", notes = "회원가입할 때 이메일 중복확인")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이메일이 중복되지 않습니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "동일한 이메일의 회원이 이미 존재합니다.")
    })
    @GetMapping("/user/signup")
    public ResponseEntity<Void> validEmail(@RequestParam @Email(message = "이메일 양식을 지켜주세요.") String email) throws DuplicateDataException {
        userService.checkDuplicateEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}


