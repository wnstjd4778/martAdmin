package com.spring.martadmin.utility;

import com.spring.martadmin.order.dto.ResponseDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.minidev.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class HttpService {

    private final RestTemplate restTemplate;

    public ResponseEntity get(String url) {
        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity get(String url, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity get(UriComponents builder) {
        return restTemplate.getForEntity(builder.toUriString(), String.class);
    }

    public ResponseEntity get(UriComponentsBuilder builder, HttpHeaders headers) {
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET, entity, String.class);
    }

    // 이 경우엔 getDeclaredField로 필드값 조회가 불가능.
    public <T,P> T post(UriComponents builder, HttpEntity<P> entity, Class<T> response) {
        return restTemplate.postForObject(builder.toUriString(), entity, response);
    }

    public ResponseEntity<ResponseDataDto<Map<String, Object>>> get(UriComponents builder, HttpHeaders httpHeaders) {
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                new ParameterizedTypeReference<ResponseDataDto<Map<String, Object>>>() {});
    }

    public JSONObject MapConverterToJson(Map<String,Object> map) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            json.put(key, value);
        }

        return json;
    }
}
