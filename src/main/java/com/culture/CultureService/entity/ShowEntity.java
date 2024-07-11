package com.culture.CultureService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //db 저장 시 아이디
    
    private String showId; //공연 아이디
    private String title; // 공연명
    private String stDate; //공연 시작일
    private String edDate; //공연 종료일

    private String cast; //출연진
    private String staff; //제작진
    private String runtime; //런타임
    private String age; //관람연령
    private String producer; //제작사 (기획제작자)
    @Column(length = 500)
    private String ticketPrice; //티켓 가격
    private String posterUrl; //포스터 이미지 경로

    private String genre; //장르
    private String state; //공연상태
    private String storyUrl; //소개 이미지
    private String time; //공연 시간

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "place_id", referencedColumnName = "placeId")
    private PlaceEntity place;
}
