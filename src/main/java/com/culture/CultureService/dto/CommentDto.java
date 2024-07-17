package com.culture.CultureService.dto;

import com.culture.CultureService.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime date;
    private String authorName;
    private String authorEmail;
    public CommentDto() {}

    public CommentDto(CommentEntity comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.date = comment.getDate();
        if (comment.getUser() != null) {
            this.authorName = comment.getUser().getName();
            this.authorEmail = comment.getUser().getEmail();
        } else if (comment.getMember() != null) {
            this.authorName = comment.getMember().getName();
            this.authorEmail = comment.getMember().getEmail();
        } else {
            this.authorName = "Unknown Author";
            this.authorEmail = "unknown";
        }
    }
}
