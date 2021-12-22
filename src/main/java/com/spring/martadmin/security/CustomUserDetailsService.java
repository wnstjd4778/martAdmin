package com.spring.martadmin.security;


import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.AdminRepository;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    // Spring Security가 User 클래스를 사용해 Authentication을 사용
    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        log.info("loadUserByUsername 호출");
        if(principal.contains("@")) {

            //User정보를 DB에서 가져온다.
            User user = userRepository.findByEmail(principal)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다."));
            log.info("loadUserByUsername DB통과");
            //DB에서 가져온 User정보를 UserPrincipal 클래스로 변경해 Spring Security로 전달한다.
            //UserPrincipal은 Spring Security의 UserDetails를 implements 하였으므로, 이제 Spring Security는 User 클래스를 사용해 Authentication을 사용 할 수 있게 되었다.
            return UserPrincipal.create(user);
        } else {

            //Admin정보를 DB에서 가져온다
            Admin admin = adminRepository.findById(principal)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 아이디로 관리자를 찾을 수 없습니다."));

            //DB에서 가져온 Admin 정보는 UserPrincipal 클래스로 변갱해 Spring Security에 전달한다.
            return UserPrincipal.create(admin);
        }
    }
}
