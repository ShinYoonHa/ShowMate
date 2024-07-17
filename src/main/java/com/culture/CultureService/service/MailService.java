package com.culture.CultureService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender emailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private MimeMessage createMessage(String to, String code) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();


        message.addRecipients(MimeMessage.RecipientType.TO, to); // 보내는 대상
        message.setSubject("GoodJob 회원가입 이메일 인증"); // 제목

        String msgg = "";
        msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> 통합 취업 정보 포탈 GoodJob 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 복사해, 회원가입 화면에 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>항상 당신의 꿈을 응원합니다. 감사합니다!<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += code + "</strong><div><br/> "; // 메일에 인증번호 넣기
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html"); // 내용, charset 타입, subtype
        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(new InternetAddress("secret07547952@gmail.com", "GoodJob_Admin")); // 보내는 사람

        return message;
    }

    public String sendSimpleMessage(String to) throws Exception {
        String code = createKey();
        MimeMessage message = createMessage(to, code);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalAccessException();
        }
        return code;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random r = new Random();

        for (int i = 0; i < 8; i++) {
            int idx = r.nextInt(3); // 0,1,2 중에 나오도록
            switch (idx) {
                case 0:
                    key.append((char) (r.nextInt(26) + 97)); // a~z
                    break;
                case 1:
                    key.append((char) (r.nextInt(26) + 65)); // A~Z
                    break;
                case 2:
                    key.append(r.nextInt(10)); // 0~9
                    break;
            }
        }
        return key.toString();
    }
    public void sendTempPasswordMail(String email, String tempPassword) {
        String subject = "임시 비밀번호 발급";
        String content = "<p>임시 비밀번호를 사용하여 로그인 후 비밀번호를 변경해주세요:</p>"
                + "<p><strong>" + tempPassword + "</strong></p>";

        sendEmail(email, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

