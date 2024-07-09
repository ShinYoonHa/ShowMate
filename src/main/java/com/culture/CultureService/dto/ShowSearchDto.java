package com.culture.CultureService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowSearchDto {
    private String searchPeriod; //조회할 기간
    private String searchAge; //관람 연령
    private String searchFee; //요금 유무
    private String searchGenre; //장르
    private String searchBy; //조회 유형(제목/장소명)
    private String searchQuery = ""; //검색어
}
