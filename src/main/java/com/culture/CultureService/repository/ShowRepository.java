package com.culture.CultureService.repository;

import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {
    ShowEntity findByShowId(String showId);
    boolean existsByShowId(String showId);
}
