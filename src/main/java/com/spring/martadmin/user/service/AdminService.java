package com.spring.martadmin.user.service;

import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // 관리자 로그인
    public void checkLogin(String id, String password) throws SessionUnstableException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new SessionUnstableException("아이디 또는 비밀번호가 일치하지 않습니다."));
        if(!passwordEncoder.matches(password, admin.getPassword())) {
            throw new SessionUnstableException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 관리자 아이디 중복 확인
    public void checkDuplicateId(String id) throws DuplicateDataException {
        Optional<Admin> findAdmin = adminRepository.findById(id);

        if(findAdmin.isPresent()) {
            throw new DuplicateDataException("동일한 아이디의 관리자가 이미 존재합니다.");
        }
    }

    // 관리자 회원가입
    @Transactional
    public Integer saveAdmin(Admin admin) throws DuplicateDataException {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);
        return admin.getNo();
    }
}
