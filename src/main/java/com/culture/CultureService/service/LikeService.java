package com.culture.CultureService.service;

import com.culture.CultureService.entity.LikeEntity;
import com.culture.CultureService.entity.Member;
import com.culture.CultureService.entity.User;
import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public List<LikeEntity> getLikesByMember(Member member) {
        return likeRepository.findByMember(member);
    }

    public List<LikeEntity> getLikesByUser(User user) {
        return likeRepository.findByUser(user);
    }

    public void toggleLike(Member member, ShowEntity show) {
        LikeEntity like = likeRepository.findByMemberAndShow(member, show);
        if (like != null) {
            likeRepository.delete(like);
        } else {
            LikeEntity newLike = new LikeEntity();
            newLike.setMember(member);
            newLike.setShow(show);
            likeRepository.save(newLike);
        }
    }

    public void toggleLike(User user, ShowEntity show) {
        LikeEntity like = likeRepository.findByUserAndShow(user, show);
        if (like != null) {
            likeRepository.delete(like);
        } else {
            LikeEntity newLike = new LikeEntity();
            newLike.setUser(user);
            newLike.setShow(show);
            likeRepository.save(newLike);
        }
    }

    public boolean isLiked(User user, Long showId) {
        ShowEntity show = new ShowEntity();
        show.setId(showId);
        return likeRepository.findByUserAndShow(user, show) != null;
    }

    public boolean isLiked(Member member, Long showId) {
        ShowEntity show = new ShowEntity();
        show.setId(showId);
        return likeRepository.findByMemberAndShow(member, show) != null;
    }
}
