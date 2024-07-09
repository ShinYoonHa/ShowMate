package com.culture.CultureService.controller;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.PostEntity;
import com.culture.CultureService.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {
    private  final PostService postService;

    @GetMapping("/posts")
    public String listPosts(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPage = postService.getPosts(pageable);
        model.addAttribute("posts", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        return "post_list";
    }
     @GetMapping("/posts/new")
    public String newPostForm(Model model){
         System.out.println("새로운 글 표시중");
        model.addAttribute("postFormDto", new PostFormDto());
        return "post_form";
     }

     @PostMapping("/posts/new")
    public String savePost(@ModelAttribute PostFormDto postFormDto){
         System.out.println("새로운 글 데이터 받음" + postFormDto);
        postService.savePost(postFormDto);
         System.out.println("redirecting,,,");
        return "redirect:/posts";
     }

     @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model){
         System.out.println("글, id 가져오는중" + id);
        PostEntity postEntity = postService.getPostById(id);
         System.out.println("가져온 글" + postEntity);
        model.addAttribute("post", postEntity);
        return "post_detail";
     }

     @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable("id") Long id, Model model){
         System.out.println("id로 편집할 글 가져오는중 : "+id);
         PostEntity postEntity = postService.getPostById(id);
         PostFormDto postFormDto = new PostFormDto();
         postFormDto.setAuthor(postEntity.getAuthor());
         postFormDto.setTitle(postEntity.getTitle());
         postFormDto.setContent(postEntity.getContent());
         postFormDto.setPostDate(postEntity.getPostDate());
         model.addAttribute("postFormDto", postFormDto);
         model.addAttribute("postId", id);
         return "post_edit_form";
     }

     @PatchMapping(value = "/posts/edit/{id}")
    public @ResponseBody ResponseEntity updatePost(@PathVariable("id") Long id, @RequestBody PostFormDto postFormDto){
         System.out.println("id로 글 업데이트중 : " + id);
         postService.updatePost(id, postFormDto);
         System.out.println("글 업데이트 성공 : " + id);
         return new ResponseEntity<Long>(id, HttpStatus.OK);
     }
    @DeleteMapping(value = "/posts/delete/{id}")
    public @ResponseBody ResponseEntity deletePost(@PathVariable("id") Long id){
        System.out.println("id로 글 삭제중 : " + id);
        postService.deletePost(id);
        System.out.println("글 삭제 성공 : " + id);
        return new ResponseEntity<Long>(id, HttpStatus.OK);
    }
}
