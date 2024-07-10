package com.culture.CultureService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwardSearchDto {
    private String searchGenre; //장르
    private String searchBy; //조회 유형(제목/수상명)
    private String searchQuery = ""; //검색어
}
