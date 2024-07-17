package com.culture.CultureService.controller;


import com.culture.CultureService.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public Map<String, Object> sendSms(@RequestParam String tel) {
        Map<String, Object> response = new HashMap<>();
        String verificationCode = smsService.generateVerificationCode();

        boolean isSent = smsService.sendVerificationCode(tel, verificationCode);
        if (isSent) {
            response.put("status", "success");
            response.put("code", verificationCode);
        } else {
            response.put("status", "failure");
        }

        return response;
    }

    @PostMapping("/verify")
    public Map<String, Object> verifySmsCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String tel = request.get("tel");
        String code = request.get("code");

        boolean isValid = smsService.verifyCode(tel, code);
        if (isValid) {
            response.put("status", "success");
        } else {
            response.put("status", "failure");
        }

        return response;
    }
}
