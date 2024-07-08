package com.culture.CultureService.service;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.Post;
import com.culture.CultureService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getPost(){
        System.out.println("모든글 가져오는중");
        return postRepository.findAll();
    }

    public void savePost(PostFormDto postFormDto){
        System.out.println("저장" + postFormDto);
        Post post = new Post();
        post.setTitle(postFormDto.getTitle()); //제목 필드 설정
        post.setContent(postFormDto.getContent());// 내용 필드 설정
        post.setPostDate(postFormDto.getPostDate()); // 날짜 필드 설정
        post.setAuthor(postFormDto.getAuthor()); // 작성자 필드 설정
        postRepository.save(post);
        System.out.println("저장완료"+ post);
    }
}
