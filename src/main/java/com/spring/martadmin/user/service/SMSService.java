package com.spring.martadmin.user.service;

import com.spring.martadmin.advice.exception.SMSException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;

@AllArgsConstructor
@Service
@Slf4j
public class SMSService {

    // 인증번호 전송
    @Transactional
    public void sendMessage(String phone, String message) throws SMSException {

        String API_KEY = "NCSU7VI0RR3W4LFK";
        String API_SECRET = "FIIRKKFNZZOQ09RJKZN3A8W8IYKZRZ6K";
        Message coolSMS = new Message(API_KEY, API_SECRET);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phone); // 누구에게 보낼건지
        params.put("from", "01065614778"); //누구 번호에서 보낼건지
        params.put("type", "SMS"); // 타입 정하기
        params.put("text", message);
        params.put("app_version", "test app 1.2"); // 이름과 서버

        try {
            log.info("message: " + message);
            JSONObject obj = (JSONObject) coolSMS.send(params);
            log.info(obj.toString());
        } catch (CoolsmsException e) {
            throw new SMSException("메세지 전송 실패");
        }
    }
}
