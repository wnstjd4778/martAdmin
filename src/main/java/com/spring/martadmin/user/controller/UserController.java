package com.spring.martadmin.user.controller;
import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SMSException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.dto.*;
import com.spring.martadmin.user.repository.AdminRepository;
import com.spring.martadmin.user.repository.UserRepository;
import com.spring.martadmin.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @ApiOperation(value = "이메일 찾기", notes = "해당 회원의 이메일 주소를 찾는다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "일치하는 회원이 존재합니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 404, message = "일치하는 회원이 존재하지 않습니다.")
    })
    @PostMapping("/find/email")
    public ResponseEntity<String> findEmail(@ApiParam("이름, 휴대폰번호 정보") @Valid @RequestBody FindRequestDto requestDto) throws NotFoundDataException {
        User user = userService.findUserByNameAndTel(requestDto.getName(), requestDto.getPhone());
        return ResponseEntity.status(HttpStatus.OK).body(user.getEmail());
    }

    @ApiOperation(value = "비밀번호 찾기 시 핸드폰 본인인증", notes = "비밀번호를 찾기 위해 본인메세지를 전송한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "전송된 인증번호 반환"),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 404, message = "일치하는 회원이 존재하지 않습니다."),
            @ApiResponse(code = 500, message = "메세지 전송 실패")
    })
    @PostMapping("/find/password/message")
    public ResponseEntity<Integer> findPassword(@ApiParam("휴대폰 번호") @Valid @RequestBody SendMessageRequestDto requestDto) throws SMSException {
        int validNum = userService.validatePhone(requestDto.getPhoneNo());
        return ResponseEntity.status(HttpStatus.OK).body(validNum);
    }

    @ApiOperation(value = "임시 비밀번호 발급", notes = "해당 회원이 존재하면 새 비밀번호를 문자로 발급한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "새 비밀번호 발급 후 전송 완료"),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 404, message = "일치하는 회원이 존재하지 않습니다."),
            @ApiResponse(code = 500, message = "메세지 전송 실패")
    })
    @PostMapping("/find/password")
    public ResponseEntity<Void> findPassword(@ApiParam("이메일, 휴대폰번호 정보") @Valid @RequestBody FindPasswordRequestDto requestDto) throws SMSException {
        User user = userService.findUserByEmailAndTel(requestDto.getEmail(), requestDto.getPhone());
        String newPassword = userService.generateTempPw(requestDto.getPhone()); // 폰번호를 받아서 임시비밀번호를 만들고 문자로 전송
        userService.changePassword(user.getNo(), newPassword);
        user.makeTrueTempPw();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "내 정보 조회", notes = "현재 인증된 유저의 정보를 가져온다.", authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 내정보가 불러와졌습니다."),
            @ApiResponse(code = 401, message = "1. 로그인이 필요합니다.\n" +
                    "2. 토큰 만료(새로운 토큰 발급)", response = AuthResponseDto.class)
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info(userPrincipal.getPrincipal());

        if (userPrincipal.getPrincipal().contains("@")) {
            UserResponseDto userResponseDto = UserResponseDto.of(userRepository.findByNo(userPrincipal.getNo()).orElseThrow(
                    () -> new SessionUnstableException("해당 유저를 찾을 수 없습니다.")
            ));
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
        } else {
            AdminResponseDto adminResponseDto = AdminResponseDto.of(adminRepository.findByNo(userPrincipal.getNo()).orElseThrow(
                    () -> new SessionUnstableException("해당 유저를 찾을 수 없습니다.")
            ));
            return ResponseEntity.status(HttpStatus.OK).body(adminResponseDto);
        }
    }


    // 추후 redis의 refresh토큰을 삭제함으로써 로그아웃 처리 구현
    /*@ApiOperation(value = "로그아웃", notes = "로그인된 계정을 로그아웃한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그아웃 되었습니다."),
            @ApiResponse(code = 401, message = "1. 로그인이 필요합니다.\n" +
                                                "2. 토큰 만료 (새로운 토큰 발급)", response = AuthResponseDto.class)
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/me/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       @ApiIgnore HttpServletRequest request) {
        String accessToken = jwtTokenProvider.extractToken(request);
        log.info("accessToken: " + accessToken);
    }*/

    @ApiOperation(value = "비밀번호 변경 전 확인", notes = "회원이 비밀번호 변경 전 비밀번호가 일치하는지 확인한다.", authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰에 해당하는 유저가 존재합니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 401, message = "토큰 만료", response = AuthResponseDto.class)
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/me/valid")
    public ResponseEntity<Boolean> verifyPassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @Valid @RequestBody VerifyPasswordRequestDto requestDto) throws SessionUnstableException {
        if (passwordEncoder.matches(requestDto.getPassword(), userPrincipal.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }

        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    @ApiOperation(value = "로그인 후 비밀번호 변경", notes = "로그인 후 임시비밀번호를 변경한다.", authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 비밀번호를 변경 후 새로운 토큰을 발급한다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 401, message = "일치하는 회원이 존재하지 않아 비밀번호 변경에 실패하였습니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/me/password")
    public ResponseEntity<AuthResponseDto> changePassword(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @ApiParam("새 비밀번호") @Valid @RequestBody ChangePasswordRequestDto requestDto,
                                                          @ApiIgnore HttpServletRequest request) throws SessionUnstableException {
        String accessToken = jwtTokenProvider.extractToken(request);

        User user = userService.changePassword(userPrincipal.getNo(), requestDto.getPassword());
        user.makeFalseTempPw();

        // redis로 refresh 토큰을 유효하지 않게 만듬

        //새로운 access 토큰 발급
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), requestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal newUserPrincipal = (UserPrincipal) authentication.getPrincipal();
        String newAccessToken = jwtTokenProvider.generateAccessToken(newUserPrincipal);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(newAccessToken));
    }

}

