package com.culture.CultureService.controller;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.PostEntity;
import com.culture.CultureService.service.PostService;
import com.culture.CultureService.service.ShowService;
import com.culture.CultureService.dto.ShowDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

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
    public String newPostFormWithShow(@PathVariable("showId") Long showId, Model model) {
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
    public String savePost(@ModelAttribute PostFormDto postFormDto) {
        System.out.println("새로운 글 데이터 받음: " + postFormDto);
        postService.savePost(postFormDto);
        System.out.println("저장된 데이타: " + postFormDto);
        System.out.println("redirecting...");
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable("id") Long id, Model model) {
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

        // 추가된 부분
        postFormDto.setCurrentPeople(postEntity.getCurrentPeople());
        postFormDto.setMaxPeople(postEntity.getMaxPeople());

        // 날짜 형식 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = postEntity.getRegTime().format(formatter);

        model.addAttribute("postFormDto", postFormDto);
        model.addAttribute("formattedDate", formattedDate); // 변환된 날짜 문자열 추가


        return "post/post_detail";
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

    @DeleteMapping(value = "/posts/delete/{id}")
    public @ResponseBody ResponseEntity deletePost(@PathVariable("id") Long id) {
        System.out.println("id로 글 삭제중: " + id);
        postService.deletePost(id);
        System.out.println("글 삭제 성공: " + id);
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }
}
