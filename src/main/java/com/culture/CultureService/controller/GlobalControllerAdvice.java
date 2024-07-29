package com.culture.CultureService.controller;

import com.culture.CultureService.entity.Member;
import com.culture.CultureService.service.MemberService;
import com.culture.CultureService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @ModelAttribute("userName")
    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String email = null;

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else if (principal instanceof DefaultOAuth2User) {
                Map<String, Object> attributes = ((DefaultOAuth2User) principal).getAttributes();

                // 카카오톡
                if (attributes.containsKey("kakao_account")) {
                    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                    if (kakaoAccount.containsKey("email")) {
                        email = (String) kakaoAccount.get("email");
                    }
                }
                // 네이버
                else if (attributes.containsKey("response")) {
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    if (response.containsKey("email")) {
                        email = (String) response.get("email");
                    }
                }
                // 구글
                else if (attributes.containsKey("email")) {
                    email = (String) attributes.get("email");
                }
            }

            if (email != null) {
                com.culture.CultureService.entity.User user = userService.findByEmail(email);
                if (user != null) {
                    return user.getName();
                } else {
                    Member member = memberService.findByEmail(email);
                    if (member != null) {
                        return member.getName();
                    }
                }
            }
        }
        return null;
    }
}
