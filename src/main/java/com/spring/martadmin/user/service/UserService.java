package com.spring.martadmin.user.service;

import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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
}
