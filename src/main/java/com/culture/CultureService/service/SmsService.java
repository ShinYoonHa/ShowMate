package com.culture.CultureService.service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.sender.number}")
    private String senderNumber;

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public boolean sendVerificationCode(String phoneNumber, String verificationCode) {
        Message coolsms = new Message(apiKey, apiSecret);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);
        params.put("from", senderNumber);
        params.put("type", "SMS");
        params.put("text", "쇼메이트 인증 번호 입니다 " + verificationCode);
        params.put("app_version", "test app 1.2");

        try {
            JSONObject result = coolsms.send(params);
            System.out.println(result.toString());
            verificationCodes.put(phoneNumber, verificationCode); // Store the verification code
            return true;
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            return false;
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String storedCode = verificationCodes.get(phoneNumber);
        return storedCode != null && storedCode.equals(code);
    }
}
