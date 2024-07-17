package com.culture.CultureService.service;

import com.culture.CultureService.dto.CommentDto;
import com.culture.CultureService.entity.CommentEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.User;
import com.culture.CultureService.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDto> getCommentByUser(User user){
        return commentRepository.findByUser(user)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getCommentByMember(Member member){
        return commentRepository.findByMember(member)
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public CommentEntity saveComment(CommentEntity comment){
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, String userEmail) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        if (comment.getUser() != null && comment.getUser().getEmail().equals(userEmail)) {
            commentRepository.deleteById(id);
        } else if (comment.getMember() != null && comment.getMember().getEmail().equals(userEmail)) {
            commentRepository.deleteById(id);
        } else {
            throw new SecurityException("User not authorized to delete this comment");
        }
    }

    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }
}
