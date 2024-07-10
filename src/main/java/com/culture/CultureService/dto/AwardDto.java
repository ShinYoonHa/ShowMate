package com.culture.CultureService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AwardDto {

    private Long id; //db 저장시 id
    private String awardId; //수상작 Id
    private String title; //수상작 제목
    private String stDate; //공연 시작일
    private String edDate; //공연 종료일
    private String placeName; // 시설명
    private String posterUrl; //포스터 사진
    private String genre; //장르
    private String state; // 공연 상태
    private String awardName; // 수상명

    public AwardDto(String awardId, String title, String stDate, String edDate, String placeName, String genre, String posterUrl){
        this.awardId = awardId;
        this.title = title;
        this.stDate = stDate;
        this.edDate = edDate;
        this.placeName = placeName;
        this.genre = genre;
        this.posterUrl = posterUrl;
    }
}
