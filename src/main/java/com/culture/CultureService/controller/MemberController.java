package com.culture.CultureService.controller;

import com.culture.CultureService.dto.CommentDto;
import com.culture.CultureService.dto.MemberFormDto;
import com.culture.CultureService.dto.ShowDto;
import com.culture.CultureService.dto.UserFormDto;
import com.culture.CultureService.entity.LikeEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static com.culture.CultureService.service.MemberService.logger;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserService userService;
    private final LikeService likeService;
    private final ShowService showService;
    private final CommentService commentService;
    private String confirm = "";
    private boolean confirmCheck = false;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }
        if (!confirmCheck) {
            model.addAttribute("errorMessage", "이메일 인증을 하세요.");
            return "member/memberForm";
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginMember() {
        return "member/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity<String> emailConfirm(@PathVariable("email") String email) throws Exception {
        confirm = mailService.sendSimpleMessage(email);
        return new ResponseEntity<>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }

    @PostMapping("/{code}/codeCheck")
    public @ResponseBody ResponseEntity<String> codeConfirm(@PathVariable("code") String code) throws Exception {
        if (confirm.equals(code)) {
            confirmCheck = true;
            return new ResponseEntity<>("인증 성공하였습니다.", HttpStatus.OK);
        }
        return new ResponseEntity<>("인증 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {  // 소셜 로그인 사용자인 경우
            String email = oAuth2User.getAttribute("email");
            com.culture.CultureService.entity.User user = userService.findByEmail(email);
            if (user != null) {
                UserFormDto userFormDto = new UserFormDto();
                userFormDto.setName(user.getName());
                userFormDto.setEmail(user.getEmail());
                model.addAttribute("userFormDto", userFormDto);
                model.addAttribute("userType", "SOCIAL");

                // 좋아요 목록 추가
                List<LikeEntity> likes = likeService.getLikesByUser(user);
                List<ShowDto> likedShows = likes.stream()
                        .map(like -> ShowDto.of(like.getShow()))
                        .collect(Collectors.toList());
                model.addAttribute("likedShows", likedShows);

                //사용자 댓글 추가
                List<CommentDto> comments = commentService.getCommentByUser(user);
                model.addAttribute("myComments",comments);
            } else {
                model.addAttribute("errorMessage", "소셜 회원 정보를 찾을 수 없습니다.");
            }
        } else if (principal != null) {  // 일반 로그인 사용자인 경우
            String email = principal.getName();
            Member member = memberService.findByEmail(email);
            if (member != null) {
                MemberFormDto memberFormDto = new MemberFormDto();
                memberFormDto.setName(member.getName());
                memberFormDto.setEmail(member.getEmail());
                memberFormDto.setAddress(member.getAddress());
                memberFormDto.setTel(member.getTel());
                model.addAttribute("memberFormDto", memberFormDto);
                model.addAttribute("userType", "NORMAL");

                // 좋아요 목록 추가
                List<LikeEntity> likes = likeService.getLikesByMember(member);
                List<ShowDto> likedShows = likes.stream()
                        .map(like -> ShowDto.of(like.getShow()))
                        .collect(Collectors.toList());
                model.addAttribute("likedShows", likedShows);
                //사용자 댓글 추가
                List<CommentDto> comments = commentService.getCommentByMember(member);
                model.addAttribute("myComments",comments);
            } else {
                model.addAttribute("errorMessage", "일반 회원 정보를 찾을 수 없습니다.");
            }
        } else {
            model.addAttribute("errorMessage", "로그인 정보가 없습니다.");
        }

        if (!model.containsAttribute("memberFormDto")) {
            model.addAttribute("memberFormDto", new MemberFormDto());
        }
        if (!model.containsAttribute("userFormDto")) {
            model.addAttribute("userFormDto", new UserFormDto());
        }

        return "member/myPage";
    }


    @PostMapping("/update")
    public String updateMember(@Valid @ModelAttribute UserFormDto userFormDto,
                               BindingResult bindingResult, Principal principal, Model model) {
        logger.debug("Received update request for user: {}", principal.getName());

        String userType = (principal instanceof OAuth2AuthenticationToken) ? "SOCIAL" : "NORMAL";
        model.addAttribute("userType", userType);

        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors found for user: {}", principal.getName());
            if (userType.equals("SOCIAL")) {
                return "member/myPage :: #socialForm";
            } else {
                return "member/myPage :: #normalForm";
            }
        }

        if (principal instanceof OAuth2AuthenticationToken) {
            String email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
            com.culture.CultureService.entity.User user = userService.findByEmail(email);
            if (user != null) {
                user.setName(userFormDto.getName());
                userService.save(user);
                logger.debug("Updated user information for email: {}", email);
            }
        } else {
            String email = principal.getName();
            MemberFormDto memberFormDto = new MemberFormDto();
            memberFormDto.setName(userFormDto.getName());
            memberFormDto.setEmail(userFormDto.getEmail());
            memberFormDto.setAddress(userFormDto.getAddress());
            memberFormDto.setTel(userFormDto.getTel());
            memberService.updateMember(email, memberFormDto);
            logger.debug("Updated member information for email: {}", email);
        }

        return "redirect:/members/mypage?updated=true";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam(value = "password", required = false) String password, Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
        String email = null;
        String provider = null;

        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
            email = (String) oAuth2Token.getPrincipal().getAttributes().get("email");
            provider = oAuth2Token.getAuthorizedClientRegistrationId();
        } else {
            email = principal.getName();
        }

        System.out.println("deleteMember called with email: " + email);

        if (email == null) {
            System.out.println("Email is null");
            model.addAttribute("errorMessage", "로그인 정보가 없습니다.");
            return "member/myPage";
        }

        if (password == null || password.isEmpty()) {
            // 소셜 로그인 사용자의 경우
            com.culture.CultureService.entity.User user = userService.findByEmail(email);
            if (user != null) {
                System.out.println("User found: " + user.getEmail());
                userService.deleteUser(user);
                if (provider != null) {
                    revokeOAuth2AccessToken(principal, provider);
                }
                System.out.println("User deleted: " + user.getEmail());
                logoutCurrentUser(request, response);
                return "redirect:/";
            } else {
                System.out.println("User not found with email: " + email);
                model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
                return "member/myPage";
            }
        } else {
            // 일반 로그인 사용자의 경우
            if (memberService.deleteMember(email, password)) {
                System.out.println("Member deleted: " + email);
                logoutCurrentUser(request, response);
                return "redirect:/";
            } else {
                System.out.println("Password mismatch for email: " + email);
                model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                return "member/myPage";
            }
        }
    }

    private void revokeOAuth2AccessToken(Principal principal, String provider) {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) principal;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, authenticationToken.getName());

        if (authorizedClient != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String revokeUrl = null;

            if ("google".equals(provider)) {
                revokeUrl = "https://accounts.google.com/o/oauth2/revoke?token=" + accessToken;
            } else if ("naver".equals(provider)) {
                revokeUrl = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&access_token=" + accessToken + "&service_provider=NAVER";
            } else if ("kakao".equals(provider)) {
                revokeUrl = "https://kapi.kakao.com/v1/user/unlink";
            }

            if (revokeUrl != null) {
                RestTemplate restTemplate = new RestTemplate();
                if ("kakao".equals(provider)) {
                    restTemplate.postForObject(revokeUrl, null, String.class, accessToken);
                } else {
                    restTemplate.postForObject(revokeUrl, null, String.class);
                }

                // Remove the authorized client from the authorized client service
                authorizedClientService.removeAuthorizedClient(provider, authenticationToken.getName());
            }
        }
    }

    private void logoutCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    @GetMapping("/find-id")
    public String findIdForm() {
        return "member/findIdForm";
    }

    @PostMapping("/find-id")
    public String findId(@RequestParam("name") String name, @RequestParam("tel") String tel, Model model) {
        String email = memberService.findEmailByNameAndTel(name, tel);
        if (email != null) {
            model.addAttribute("email", email);
        } else {
            model.addAttribute("errorMessage", "해당 정보로 가입된 회원을 찾을 수 없습니다.");
        }
        return "member/findIdResult";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm() {
        return "member/resetPasswordForm";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        try {
            String tempPassword = memberService.resetPassword(email);
            mailService.sendTempPasswordMail(email, tempPassword);
            model.addAttribute("message", "임시 비밀번호를 이메일로 보냈습니다.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "해당 이메일로 가입된 회원을 찾을 수 없습니다.");
        }
        return "member/resetPasswordResult";
    }
}
