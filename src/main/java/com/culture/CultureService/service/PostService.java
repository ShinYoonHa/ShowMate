package com.culture.CultureService.service;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.Post;
import com.culture.CultureService.repository.PostRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getPosts(){
        System.out.println("모든글 가져오는중");
        return postRepository.findAll();
    }

    public Page<Post> getPosts(Pageable pageable){
        return postRepository.findAll(pageable);
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

    public Post getPostById(Long id){
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }else {
            throw new IllegalArgumentException("잘못된 글입니다" + id);
        }
    }

    public void updatePost(Long id, PostFormDto postFormDto){
        Post post = postRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
        post.setTitle(postFormDto.getTitle());
        post.setAuthor(postFormDto.getAuthor());
        post.setContent(post.getContent());
        post.setPostDate(post.getPostDate());
        postRepository.save(post);
        System.out.println("글 업데이트 성공 : " + post);
    }

    public void deletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postRepository.delete(post);
        System.out.println("글 삭제 성공 : " + id);
    }
}
