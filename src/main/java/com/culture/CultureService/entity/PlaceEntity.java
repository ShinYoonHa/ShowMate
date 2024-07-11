package com.culture.CultureService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PlaceEntity { //공연시설 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //db 저장 시 아이디

    private String placeId; //공연시설 아이디
    private String name; //시설명
    private String feature; //시설 특성
    private String seat; //좌석수
    private String address; //주소
    private String url; //홈페이지 주소
    private String longitude; //경도
    private String latitude; //위도

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowEntity> shows = new ArrayList<>();
}
