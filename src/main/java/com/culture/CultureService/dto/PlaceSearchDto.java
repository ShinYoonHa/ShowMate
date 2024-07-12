package com.culture.CultureService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceSearchDto {
    private String searchBy; //조회 유형(제목/주소)
    private String searchQuery = ""; //검색어
}
