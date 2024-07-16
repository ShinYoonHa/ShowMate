package com.culture.CultureService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PostFormDto {
    private Long id; //
    private String title; //제목
    private String content; //내용
    private String author; //작성자

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate postDate; //새로운 날짜 피트

    private LocalDateTime regTime; // 작성시간 추가


    // 공연 정보 추가
    private String showId;
    private String showTitle;
    private String showPeriod;
    private String showGenre;
    private String showPosterUrl;

    @Override
    public String toString() {
        return "PostFormDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", postDate=" + postDate +
                ", regTime=" + regTime +
                ", showId='" + showId + '\'' +
                ", showTitle='" + showTitle + '\'' +
                ", showPeriod='" + showPeriod + '\'' +
                ", showGenre='" + showGenre + '\'' +
                ", showPosterUrl='" + showPosterUrl + '\'' +
                '}';
    }

}
