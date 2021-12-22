package com.spring.martadmin.security.oauth2.user;

import java.util.HashMap;
import java.util.Map;

public class KakaoOauth2UserInfo extends OAuth2UserInfo{

    public KakaoOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        HashMap<String, String> accountMap = (HashMap<String, String>) attributes.get("kakao_account");
        return accountMap.get("email");
    }

    @Override
    public String getName() {
        HashMap<String, String> propertyMap = (HashMap<String, String>) attributes.get("properties");
        return propertyMap.get("nickname");
    }
}
