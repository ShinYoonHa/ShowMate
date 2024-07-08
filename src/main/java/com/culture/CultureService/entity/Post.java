package com.culture.CultureService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post")
public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String author; // 작성자 필드 추가

    @Column(nullable = false)
    private String title; // 제목 필드

    @Column(nullable = false)
    private String content; // 내용 필드

    private LocalDate postDate; // 날짜 필드

    private LocalDateTime regTime; // 작성시간

    @PrePersist
    public void prePersist(){
        if(this.regTime == null) {
            this.regTime = LocalDateTime.now(); //현재 시간으로 설정
        }

    }
}
