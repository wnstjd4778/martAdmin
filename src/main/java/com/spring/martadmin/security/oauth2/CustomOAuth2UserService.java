package com.spring.martadmin.security.oauth2;


import com.spring.martadmin.advice.exception.DuplicateDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.security.oauth2.user.OAuth2UserInfo;
import com.spring.martadmin.security.oauth2.user.OAuth2UserInfoFactory;
import com.spring.martadmin.user.domain.AuthProvider;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    //소셜 로그인 시도


    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            ex.printStackTrace(); // OAuth2 log 확인용
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    // 동일 회원 유무 확인
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        // 현재 진행중인 서비스를 구분하는 코드
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        //로그인 진행시 키가되는 pk값을 의미
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        if(oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail() == "") {
            throw new SessionUnstableException("회원정보에 이메일이 존재하지 않습니다.");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getSnsType().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new DuplicateDataException("이미" + user.getSnsType() + " 를 통해 등록된 이메일입니다. " +
                        user.getSnsType() + " 으로 로그인 해주세요.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    //소셜로그인을 통한 유저 생성
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .snsType(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))
                .snsKey(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .build();
        return  userRepository.save(user);
    }

    //기존 유저 정보 업데이트
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setEmail(oAuth2UserInfo.getEmail());
        existingUser.setName(oAuth2UserInfo.getName());
        return userRepository.save(existingUser);
    }
}
