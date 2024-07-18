package com.culture.CultureService.service;

import com.culture.CultureService.dto.PostFormDto;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.PostEntity;
import com.culture.CultureService.repository.MemberRepository;
import com.culture.CultureService.repository.PostRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null; // 이메일 초기화
        if (authentication.getPrincipal() instanceof UserDetails) {
            // 일반 로그인의 경우
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();  // UserDetails에서는 username이 이메일로 설정될 수 있음
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            // 소셜 로그인의 경우
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            email = oauthUser.getAttribute("email");  // OAuth2User에서는 getAttribute를 사용하여 이메일 접근
        }

        if (email == null) {
            throw new IllegalStateException("사용자 이메일을 가져올 수 없습니다.");
        }

        System.out.println("저장" + postFormDto);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postFormDto.getTitle()); // 제목 필드 설정
        postEntity.setContent(postFormDto.getContent()); // 내용 필드 설정
        postEntity.setPostDate(postFormDto.getPostDate()); // 날짜 필드 설정
        postEntity.setAuthor(email); // 작성자 이메일 설정
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

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername(); // 일반 로그인
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User)principal).<String>getAttribute("email"); // 소셜 로그인
        }
        return null;
    }

    public String applyPost(Long id, String username) {
        // ID로 게시물을 찾고, 없을 경우 EntityNotFoundException 을 발생
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        // 게시물의 작성자 이메일과 현재 사용자의 이메일 가져옴
        String authorEmail = postEntity.getAuthor();
        String email = getCurrentUserEmail();
        // 현재 사용자의 이메일로 회원 정보를 조회
        Member member = memberRepository.findByEmail(email);
        // 자신이 작성한 게시물에 신청하는 경우 'self' 반환
        if (authorEmail.equals(email)) {
            return "self";
        }
        // 게사물의 현재 인원이 최대 인원을 초과하는 경우 'max'를 반환
        if (postEntity.getCurrentPeople() >= postEntity.getMaxPeople()) {
            return "max"; // 최대 정원 초과
        }
        // 현재 인원 수를 증가시키고, 회원을 게시물의 멤버 목록에 추가
        postEntity.setCurrentPeople(postEntity.getCurrentPeople() + 1);
        postEntity.getMembers().add(member);
        // 변경된 게시물 정보 저장
        postRepository.save(postEntity);
        return "success";
    }

    public String cancelApplyPost(Long id, String username) {
        //ID로 게시물 찾고, 없을 경우 EntityNotFoundException 발생
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        // 자신이 작성한 게시물의 신청을 취소하는 경우 'self' 반환
        if (postEntity.getAuthor().equals(username)) {
            return "self"; // 자신이 작성한 게시물
        }
        // 게시물의 현재 인원이 0 이하인 경우 'min' 반환
        if (postEntity.getCurrentPeople() <= 0) {
            return "min"; // 최소 인원 미만
        }
        // 현재 인원 수를 감소, 해당 회원을 게시물의 멤버 목록에서 제거
        postEntity.setCurrentPeople(postEntity.getCurrentPeople() - 1);
        Member member = memberRepository.findByEmail(username);
        // 해당 회원을 게시물의 멤버 목록에서 찾아 제거
        // 게시물에 등록된 회원 목록 가져옴
        List<Member> lists = postEntity.getMembers();
        // 찾는 회원의 인덱스 초기화 회원을 찾기 못할 경우를 대비해-1로 설정
        int index = -1;
        // 회원 목록을 돌면서 특정 회원 ID와 목록 내의 회원 ID가 일치하는지 확인
        for(int i =0;i<lists.size();i++){
            if(member.getId() == lists.get(i).getId()){
                index = i; // 일치하는 회원의 인덱스 저장
                break;// 일치하는 회원을 찾은 후 종료
            }
        }
        // index가 -1이 아니면, 유요한 인덱스 찾은것
        if(index != -1){
            // 유요한 인덱스가 존재하므로, list에서 해당 인덱스의 Member 객체 제거
            lists.remove(index);
        }
        // 변경된 게시물 정보를 저장
        postRepository.save(postEntity);
        return "success";
    }

}
