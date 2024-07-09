package com.culture.CultureService.service;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.PostEntity;
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

    public List<PostEntity> getPosts(){
        System.out.println("모든글 가져오는중");
        return postRepository.findAll();
    }

    public Page<PostEntity> getPosts(Pageable pageable){
        return postRepository.findAll(pageable);
    }

    public void savePost(PostFormDto postFormDto){
        System.out.println("저장" + postFormDto);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postFormDto.getTitle()); //제목 필드 설정
        postEntity.setContent(postFormDto.getContent());// 내용 필드 설정
        postEntity.setPostDate(postFormDto.getPostDate()); // 날짜 필드 설정
        postEntity.setAuthor(postFormDto.getAuthor()); // 작성자 필드 설정
        postRepository.save(postEntity);
        System.out.println("저장완료"+ postEntity);
    }

    public PostEntity getPostById(Long id){
        Optional<PostEntity> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }else {
            throw new IllegalArgumentException("잘못된 글입니다" + id);
        }
    }

    public void updatePost(Long id, PostFormDto postFormDto){
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
        postEntity.setTitle(postFormDto.getTitle());
        postEntity.setAuthor(postFormDto.getAuthor());
        postEntity.setContent(postEntity.getContent());
        postEntity.setPostDate(postEntity.getPostDate());
        postRepository.save(postEntity);
        System.out.println("글 업데이트 성공 : " + postEntity);
    }

    public void deletePost(Long id){
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postRepository.delete(postEntity);
        System.out.println("글 삭제 성공 : " + id);
    }
}
