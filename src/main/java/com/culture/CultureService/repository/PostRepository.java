package com.culture.CultureService.repository;

import com.culture.CultureService.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p FROM PostEntity p WHERE " +
            "p.title LIKE %:keyword% OR " +
            "p.content LIKE %:keyword% OR " +
            "p.author LIKE %:keyword% OR " +
            "p.showTitle LIKE %:keyword%")
    Page<PostEntity> findByKeyword(String keyword, Pageable pageable);
    List<PostEntity> findAllByOrderByRegTimeDesc();
    Page<PostEntity> findAllByOrderByRegTimeDesc(Pageable pageable);
}
