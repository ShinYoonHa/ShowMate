package com.culture.CultureService.controller;

import com.culture.CultureService.dto.CommentDto;
import com.culture.CultureService.entity.CommentEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.User;
import com.culture.CultureService.service.CommentService;
import com.culture.CultureService.service.MemberService;
import com.culture.CultureService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @PostMapping("/add")
    public Map<String, Object> addComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal OAuth2User oAuth2User, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = null;
            Member member = null;
            if (oAuth2User != null) {
                String email = oAuth2User.getAttribute("email");
                user = userService.findByEmail(email);
            } else if (principal != null) {
                String email = principal.getName();
                member = memberService.findByEmail(email);
            }

            if (user != null) {
                CommentEntity comment = new CommentEntity();
                comment.setUser(user);
                comment.setContent(commentDto.getContent());
                comment.setDate(LocalDateTime.now());
                commentService.saveComment(comment);
                response.put("success", true);
                response.put("comment", new CommentDto(comment));
            } else if (member != null) {
                CommentEntity comment = new CommentEntity();
                comment.setMember(member);
                comment.setContent(commentDto.getContent());
                comment.setDate(LocalDateTime.now());
                commentService.saveComment(comment);
                response.put("success", true);
                response.put("comment", new CommentDto(comment));
            } else {
                response.put("success", false);
                response.put("message", "User or Member not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public Map<String,Object> deleteComment(@PathVariable Long id, @AuthenticationPrincipal OAuth2User oAuth2User, Principal principal) {
        Map<String,Object> response = new HashMap<>();
        try {
            String email = null;
            if (oAuth2User != null) {
                email = oAuth2User.getAttribute("email");
            } else if (principal != null) {
                email = principal.getName();
            }

            if (email != null) {
                commentService.deleteComment(id, email);
                response.put("success",true);
            } else {
                response.put("success",false);
                response.put("message","User not authenticated");
            }
        } catch (Exception e){
            response.put("success",false);
            response.put("message",e.getMessage());
        }
        return response;
    }

    @GetMapping("/list")
    public List<CommentDto> getComments() {
        return commentService.getAllComments();
    }
}
