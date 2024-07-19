package com.culture.CultureService.controller;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.PostEntity;
import com.culture.CultureService.service.MemberService;
import com.culture.CultureService.service.PostService;
import com.culture.CultureService.service.ShowService;
import com.culture.CultureService.dto.ShowDto;
import com.culture.CultureService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ShowService showService;

    @ModelAttribute
    public void addCsrfToken(Model model, CsrfToken token) {
        model.addAttribute("_csrf", token);
    }

    @GetMapping("/posts")
    public String listPosts(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage;
        if (keyword != null && !keyword.isEmpty()) {
            postPage = postService.searchPosts(keyword, pageable);
        } else {
            postPage = postService.getPostsByRegTimeDesc(pageable);
        }
        model.addAttribute("posts", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "post/post_list";
    }

    @GetMapping("/posts/new/showId={showId}")
    public String newPostFormWithShow(@PathVariable("showId") Long showId, Model model, Principal principal) {
        if (principal == null) {  // 사용자가 로그인하지 않은 경우
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        System.out.println("새로운 글 표시중");
        ShowDto showDto = showService.getShowDetail(showId);
        PostFormDto postFormDto = new PostFormDto();
        postFormDto.setShowId(showDto.getId().toString());
        postFormDto.setShowTitle(showDto.getTitle());
        postFormDto.setShowPeriod(showDto.getStDate() + " ~ " + showDto.getEdDate());
        postFormDto.setShowGenre(showDto.getGenre());
        postFormDto.setShowPosterUrl(showDto.getPosterUrl()); // 추가된 부분
        model.addAttribute("postFormDto", postFormDto);
        return "post/post_form";
    }

    @PostMapping("/posts/new")
    public String savePost(@ModelAttribute PostFormDto postFormDto, Principal principal) {
        if (principal != null) {
            String email = principal.getName();  // 현재 로그인한 사용자의 이메일을 가져옵니다.
            postFormDto.setAuthor(email);        // PostFormDto에 작성자 정보로 이메일을 설정합니다.
            postService.savePost(postFormDto);      // 수정된 PostFormDto로 게시글을 저장합니다.
        } else {
            // 로그인하지 않은 경우 처리
            return "redirect:/login";
        }
        return "redirect:/posts";
    }


    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable("id") Long id, Model model, Authentication authentication,Principal principal) {
        PostEntity postEntity = postService.getPostById(id);

        PostFormDto postFormDto = new PostFormDto();
        postFormDto.setId(postEntity.getId());
        postFormDto.setAuthor(postEntity.getAuthor());
        postFormDto.setTitle(postEntity.getTitle());
        postFormDto.setContent(postEntity.getContent());
        postFormDto.setPostDate(postEntity.getPostDate());
        postFormDto.setRegTime(postEntity.getRegTime()); // LocalDateTime으로 설정
        postFormDto.setShowId(postEntity.getShowId());
        postFormDto.setShowTitle(postEntity.getShowTitle());
        postFormDto.setShowPeriod(postEntity.getShowPeriod());
        postFormDto.setShowGenre(postEntity.getShowGenre());
        postFormDto.setShowPosterUrl(postEntity.getShowPosterUrl());

        // 현재인원과 최대 인원 정보 dto에 추가
        postFormDto.setCurrentPeople(postEntity.getCurrentPeople());
        postFormDto.setMaxPeople(postEntity.getMaxPeople());

        // 날짜 형식 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = postEntity.getRegTime().format(formatter);

        // postEntity에서 관련된 모든 회원정보 ㄱㅏ져옴
        List<Member> lists = postEntity.getMembers();
        // 로그인한 사용자가 목록에 있는지 확인하기 위한 변수 초기화
        int value = 0;
        // 모든 회원을 현재 로그인한 사용자와 이메일이 일치하는지 확인
        for(Member m : lists){
            //principal이 null이 아닐경우
            if(principal != null) {
                // principal의 이름(이메일)이 회원의 이메일과 일치할 경우
                if (principal.getName().equals(m.getEmail())) {
                    value = 1; // 사용자가 목록에 있다는 것을 표시
                    break;// 반복문 탈출
                }
            }
        }

        model.addAttribute("postFormDto", postFormDto);
        model.addAttribute("formattedDate", formattedDate); // 변환된 날짜 문자열 추가
        model.addAttribute("post", postEntity);
        model.addAttribute("currentUserEmail", getCurrentUserEmail(authentication));
        // 'check' 이라는 이름으로 'value' 값 모델에 추가
        // html 사용자 권한 확인 로직에 사용
        model.addAttribute("check",value);

        return "post/post_detail";
    }
    private String getCurrentUserEmail(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // 일반 로그인
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).<String>getAttribute("email"); // 소셜 로그인
        }
        return null;
    }

    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable("id") Long id,Model model) {
        System.out.println("id로 편집할 글 가져오는중: " + id);
        PostEntity postEntity = postService.getPostById(id);
        PostFormDto postFormDto = new PostFormDto();
        postFormDto.setAuthor(postEntity.getAuthor());
        postFormDto.setTitle(postEntity.getTitle());
        postFormDto.setContent(postEntity.getContent());
        postFormDto.setPostDate(postEntity.getPostDate());

        // 공연 정보 설정
        postFormDto.setShowId(postEntity.getShowId());
        postFormDto.setShowTitle(postEntity.getShowTitle());
        postFormDto.setShowPeriod(postEntity.getShowPeriod());
        postFormDto.setShowGenre(postEntity.getShowGenre());
        postFormDto.setShowPosterUrl(postEntity.getShowPosterUrl());

        // 추가된 부분
        postFormDto.setCurrentPeople(postEntity.getCurrentPeople());
        postFormDto.setMaxPeople(postEntity.getMaxPeople());

        model.addAttribute("postFormDto", postFormDto);
        model.addAttribute("postId", id);
        return "post/post_edit_form";
    }

    @PatchMapping(value = "/posts/edit/{id}")
    public @ResponseBody ResponseEntity updatePost(@PathVariable("id") Long id, @RequestBody PostFormDto postFormDto) {
        System.out.println("id로 글 업데이트중: " + id);
        postService.updatePost(id, postFormDto);
        System.out.println("글 업데이트 성공: " + id);
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }

    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable("id") Long id, @ModelAttribute PostFormDto postFormDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        String currentUserEmail = extractUserEmail(authentication);
        PostEntity post = postService.getPostById(id);
        if (!post.getAuthor().equals(currentUserEmail)) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다");
            return "redirect:/posts";
        }
        postService.updatePost(id, postFormDto);
        return "redirect:/posts";
    }

    @DeleteMapping("/posts/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") Long id, Authentication authentication) {
        String currentUserEmail = extractUserEmail(authentication);
        PostEntity post = postService.getPostById(id);
        if (!post.getAuthor().equals(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        postService.deletePost(id);
        return ResponseEntity.ok("삭제성공");
    }

    private String extractUserEmail(Authentication authentication) {
        if (authentication == null) {
            return null; // 인증 정보 없음
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername(); // 일반 로그인에서는 username이 이메일
        } else if (principal instanceof OAuth2User) {
            return (String)((OAuth2User)principal).getAttributes().get("email"); // 소셜 로그인에서는 attributes 맵에서 이메일 추출
        }
        return null;
    }
    // '/posts/apply/{id}' 경로로 POST 요청 오면 메서드 호출
    // 게시물에 대한 신청 처리
    @PostMapping("/posts/apply/{id}")
    @ResponseBody
    public ResponseEntity<?> applyPost(@PathVariable("id") Long id, Principal principal) {
        // 사용자 인증 여부 확인
        if (principal == null) {
            // principal 객체가 null일 경우, 로그인 필요하다는 응답 보냄
            return ResponseEntity.ok("login");
        }
        // postService를 통해 신청 로직을 처리, 결과 받음
        String result = postService.applyPost(id, principal.getName());
        if (result.equals("success")) {
            // 신청 완료시 해당 게시물의 현재 인원 수 반환
            PostEntity post = postService.getPostById(id);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("currentPeople", post.getCurrentPeople());
            }});
        } else {
            // 신청 실패시 실패 원인을 반환
            return ResponseEntity.ok(result);
        }
    }
    // '/posts/cancel/{id}' 경로로 POST 요청이 오면 메서드 호출
    //게시물에 대한 신청 취소 처리
    @PostMapping("/posts/cancel/{id}")
    @ResponseBody
    public ResponseEntity<?> cancelApplyPost(@PathVariable("id") Long id, Principal principal) {
        // 사용자 인증 여부 확인
        if (principal == null) {
            // principal 객체가 null일 경우, 로그인이 필요 응답 보냄
            return ResponseEntity.ok("login");
        }
        // postService를 통해 신청 취소로직 처리하고 결과 받음
        String result = postService.cancelApplyPost(id, principal.getName());
        if (result.equals("success")) {
            // 신청 취소성공시, 해당 게시물의 현재 인원수 반환
            PostEntity post = postService.getPostById(id);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("currentPeople", post.getCurrentPeople());
            }});
        } else {
            // 신청 취소실패시 실패 원인 반환
            return ResponseEntity.ok(result);
        }
    }

}
