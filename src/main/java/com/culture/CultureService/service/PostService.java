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
        System.out.println("모든 글 가져오는 중");
        return postRepository.findAll();
    }

    public Page<PostEntity> getPosts(Pageable pageable){
        return postRepository.findAll(pageable);
    }

    // 추가된 메서드: 최신 순으로 게시물 가져오기
    public Page<PostEntity> getPostsByRegTimeDesc(Pageable pageable) {
        return postRepository.findAllByOrderByRegTimeDesc(pageable);
    }

    public void savePost(PostFormDto postFormDto){
        System.out.println("저장" + postFormDto);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postFormDto.getTitle()); // 제목 필드 설정
        postEntity.setContent(postFormDto.getContent()); // 내용 필드 설정
        postEntity.setPostDate(postFormDto.getPostDate()); // 날짜 필드 설정
        postEntity.setAuthor(postFormDto.getAuthor()); // 작성자 필드 설정
        postEntity.setCurrentPeople(postFormDto.getCurrentPeople()); // 현재인원 설정
        postEntity.setMaxPeople(postFormDto.getMaxPeople()); // 정원 설정

        // 공연 정보 설정
        postEntity.setShowId(postFormDto.getShowId());
        postEntity.setShowTitle(postFormDto.getShowTitle());
        postEntity.setShowPeriod(postFormDto.getShowPeriod());
        postEntity.setShowGenre(postFormDto.getShowGenre());
        postEntity.setShowPosterUrl(postFormDto.getShowPosterUrl());

        postRepository.save(postEntity);
        System.out.println("저장 완료" + postEntity);
    }

    public PostEntity getPostById(Long id){
        Optional<PostEntity> post = postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        } else {
            throw new IllegalArgumentException("잘못된 글입니다: " + id);
        }
    }

    public void updatePost(Long id, PostFormDto postFormDto){
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
        postEntity.setTitle(postFormDto.getTitle());
        postEntity.setAuthor(postFormDto.getAuthor());
        postEntity.setContent(postFormDto.getContent());
        postEntity.setPostDate(postFormDto.getPostDate());
        postEntity.setCurrentPeople(postFormDto.getCurrentPeople()); // 현재인원 업데이트
        postEntity.setMaxPeople(postFormDto.getMaxPeople()); // 정원 업데이트

        // 공연 정보 업데이트
        postEntity.setShowId(postFormDto.getShowId());
        postEntity.setShowTitle(postFormDto.getShowTitle());
        postEntity.setShowPeriod(postFormDto.getShowPeriod());
        postEntity.setShowGenre(postFormDto.getShowGenre());
        postEntity.setShowPosterUrl(postFormDto.getShowPosterUrl());

        postRepository.save(postEntity);
        System.out.println("글 업데이트 성공: " + postEntity);
    }

    public void deletePost(Long id){
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        postRepository.delete(postEntity);
        System.out.println("글 삭제 성공: " + id);
    }

    public Page<PostEntity> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByKeyword(keyword, pageable);
    }


}
