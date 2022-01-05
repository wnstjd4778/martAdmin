package com.spring.martadmin.user.service;

import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SMSException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SMSService smsService;

    // 로그인
    public User checkLogIn(String email, String password) throws SessionUnstableException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SessionUnstableException("아이디 또는 비밀번호가 일치하지 않습니다."));
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new SessionUnstableException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // 회원가입
    @Transactional
    public Integer saveUser(User user) throws DuplicateDataException {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 패스워드 인코딩을 써서 암호화한다.
        userRepository.save(user);
        return user.getNo();
    }

    // 로컬 이메일 중복 확인
    public void checkDuplicateEmail(String email) throws DuplicateDataException {
        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isPresent()) {
            throw new DuplicateDataException("동일한 이메일의 회원이 이미 존재합니다.");
        }
    }

    // 휴대폰 중복 확인
    public void checkDuplicateTel(String tel) throws DuplicateDataException {
        Optional<User> findUser = userRepository.findByTel(tel);

        if(findUser.isPresent()) {
            throw new DuplicateDataException("동일한 휴대폰 번호의 회원이 이미 존재합니다.");
        }
    }

    //본인확인 인증번호를 핸드폰으로 전송
    public int validatePhone(String phoneNo) throws SMSException {
        int validNum;
        do {
            validNum = (int) (Math.random() * 100000);
        } while (validNum < 10000);

        String message = "[인증번호]\n" + validNum;
        smsService.sendMessage(phoneNo, message);
        return validNum;
    }

    // 이름과 휴대폰 번호로 유저 정보 찾기
    public User findUserByNameAndTel(String name, String phone) throws NotFoundDataException {
        User user = userRepository.findByNameAndTel(name, phone)
                .orElseThrow(() -> new NotFoundDataException("해당하는 회원을 찾을 수 없습니다."));
        return user;
    }

    // 이메일과 휴대폰 번호로 유저 찾기
    public User findUserByEmailAndTel(String email, String phone) throws NotFoundDataException {
        User user = userRepository.findByEmailAndTel(email, phone)
                .orElseThrow(() -> new NotFoundDataException("해당하는 회원을 찾을 수 없습니다."));

        return user;
    }

    //임시 비밀번호를 핸드폰으로 전송
    public String generateTempPw(String phone_no) {
        String pw = "";
        for(int i = 0; i < 12; i++) {
            pw += (char) ((Math.random() * 26) + 97);
        }
        String message = "[임시 비밀번호]\n" + pw;
        smsService.sendMessage(phone_no, message);
        return pw;
    }

    //비밀번호 변경
    @Transactional
    public User changePassword(int no, String password) throws SessionUnstableException{
            User user = userRepository.findByNo(no)
                    .orElseThrow(() -> new SessionUnstableException("아이디 또는 비밀번호가 일치하지 않습니다."));

            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            return user;
    }
}
