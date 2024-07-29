package com.culture.CultureService.service;

import com.culture.CultureService.entity.Member;
import com.culture.CultureService.repository.MemberRepository;
import com.culture.CultureService.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @Value("${coolsms.api.noticeKey}")
    private String noticeApiKey;

    @Value("${coolsms.api.secret.noticeKey}")
    private String noticeApiSecret;

    @Value("${coolsms.sender.tel}")
    private String noticeSenderTel;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    private DefaultMessageService messageService;
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public SmsService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }
    @PostConstruct
    public void init() {
        // coolsms 계정 내 등록된 API 키, API Secret Key
        this.messageService = NurigoApp.INSTANCE.initialize(noticeApiKey, noticeApiSecret, "https://api.coolsms.co.kr");
    }

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

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername(); // 일반 로그인
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User)principal).<String>getAttribute("email"); // 소셜 로그인
        }
        return null;
    }

    public SingleMessageSentResponse sendNotice(String postTitle, String postDate) {
        net.nurigo.sdk.message.model.Message message = new net.nurigo.sdk.message.model.Message();
        //현재 로그인된 사용자의 이메일
        String email = getCurrentUserEmail();
        // 현재 사용자의 이메일로 회원 정보를 조회
        Member member = memberRepository.findByEmail(email);
        //찾은 사용자의 전화번호
        String tel = member.getTel();

        // 발신번호 및 수신번호는 01012345678 형태
        message.setFrom(noticeSenderTel); //발신번호 입력
        message.setTo(tel); //수신번호 입력
        String msg = "[참가신청완료]" + "\n제목: " + postTitle + "\n일시: " + postDate;
        message.setText(msg);

        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }

}
