package com.culture.CultureService.repository;

import com.culture.CultureService.entity.CommentEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    List<CommentEntity> findByUser(User user);
    List<CommentEntity> findByMember(Member member);

}
