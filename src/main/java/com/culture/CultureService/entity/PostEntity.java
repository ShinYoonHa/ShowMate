package com.culture.CultureService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "author_email", nullable = true)
    private String author; // 작성자 필드 추가

    @Column(nullable = false)
    private String title; // 제목 필드

    @Column(nullable = false)
    private String content; // 내용 필드

    private LocalDateTime postDate; // 날짜 필드

    private LocalDateTime regTime; // 작성시간

    // 공연 정보 필드 추가
    private String showId;
    private String showTitle;
    private String showPeriod;
    private String showGenre;
    private String showPosterUrl;


    @ManyToMany
    @JoinTable(
            name = "member_posts", inverseJoinColumns =  @JoinColumn(name = "member_id"),
            joinColumns = @JoinColumn(name = "id")
    )
    private List<Member> members; // 'Member' 엔티티의 인스턴스 목록 저장, 관계된'Member' 객체 포함

    // 현재인원 및 정원 필드 추가
    private int currentPeople = 1; //현재인원
    private int maxPeople;
    // viewCount 필드 추가
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0; // 조회수 필드 추가

    @PrePersist
    public void prePersist(){
        if(this.regTime == null) {
            this.regTime = LocalDateTime.now(); // 현재 시간으로 설정
        }
    }
}

