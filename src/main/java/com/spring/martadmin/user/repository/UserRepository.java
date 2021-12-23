package com.spring.martadmin.user.repository;

import com.spring.martadmin.user.domain.AuthProvider;
import com.spring.martadmin.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> findByNo(Integer no);

    public Optional<User> findByTel(String tel);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByEmailAndSnsType(String email, AuthProvider authProvider);

    public Optional<User> findByNameAndTel(String name, String tel);

    public Optional<User> findByEmailAndTel(String email, String tel);
}
