package com.culture.CultureService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class PostFormDto {
    private String title; //제목
    private String content; //내용
    private String author; //작성자

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate postDate; //새로운 날짜 피트
}
