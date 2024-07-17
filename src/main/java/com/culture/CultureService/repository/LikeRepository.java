package com.culture.CultureService.repository;

import com.culture.CultureService.entity.LikeEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.User;
import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    List<LikeEntity> findByMember(Member member);
    List<LikeEntity> findByUser(User user);
    LikeEntity findByMemberAndShow(Member member, ShowEntity show);
    LikeEntity findByUserAndShow(User user, ShowEntity show);
}
