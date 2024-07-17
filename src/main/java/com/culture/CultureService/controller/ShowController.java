package com.culture.CultureService.controller;

import com.culture.CultureService.dto.ShowDto;
import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.entity.User;
import com.culture.CultureService.service.LikeService;
import com.culture.CultureService.service.MemberService;
import com.culture.CultureService.service.ShowService;
import com.culture.CultureService.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/show")
public class ShowController {
    private final ShowService showService;
    private final LikeService likeService;
    private final MemberService memberService;
    private final UserService userService;

    @GetMapping(value = {"", "/page={page}"})
    public String showList(ShowSearchDto showSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 20개
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 15);

        Page<ShowEntity> shows = showService.getShowListPage(showSearchDto, pageable);
        model.addAttribute("shows", shows);
        model.addAttribute("showSearchDto", showSearchDto);
        model.addAttribute("maxPage", 10);
        return "show/showList";
    }

    //공연상세페이지
    @GetMapping(value = "/id={id}")
    public String showDetail(@PathVariable("id") Long id, Model model) {
        try {
            ShowDto showDto = showService.getShowDetail(id);
            model.addAttribute("showDto", showDto);
            return "show/showDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 공연입니다.");
            return "show/showList";
        }
    }
    //공연상세페이지
    @PostMapping(value = "/showId={showId}/{isLiked}")
    public String showLike(@PathVariable("showId") String showId, @PathVariable("isLiked") String isLiked, Model model) {
        try {
            System.out.println("좋아요 상태: isLiked = " + isLiked);

            ShowDto showDto = showService.getShowDetail(showId);
            model.addAttribute("showDto",showDto);
            return "show/showDetail";
        } catch (EntityNotFoundException e) {
            System.out.println("에러에러에러 발생");
            return "show/showList";
        }
    }

    //공연상세페이지
    @GetMapping(value = "/showId={showId}")
    public String showDetail(@PathVariable("showId") String showId, Model model) {
        try {
            ShowDto showDto = showService.getShowDetail(showId);
            model.addAttribute("showDto", showDto);
            return "show/showDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 공연입니다.");
            return "show/showList";
        }
    }

    //좋아요 기능
    @PostMapping(value = "/showId={showId}/{isLiked}")
    public ResponseEntity<?> showLike(@PathVariable("showId") String showId, @PathVariable("isLiked") boolean isLiked, Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User) {
        try {
            ShowEntity showEntity = showService.getShowByShowId(showId);
            if (oAuth2User != null) {  // 소셜 로그인 사용자인 경우
                String email = oAuth2User.getAttribute("email");
                User user = userService.findByEmail(email);
                if (user != null) {
                    likeService.toggleLike(user, showEntity);
                }
            } else if (principal != null) {  // 일반 로그인 사용자인 경우
                String email = principal.getName();
                Member member = memberService.findByEmail(email);
                if (member != null) {
                    likeService.toggleLike(member, showEntity);
                }
            }
            return ResponseEntity.ok().body(Collections.singletonMap("success", true));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("success", false));
        }
    }
}

