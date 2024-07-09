package com.culture.CultureService.repository;

import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<ShowEntity, Long>, ShowRepositoryCustom {
    ShowEntity findByShowId(String showId);
    boolean existsByShowId(String ShowId);
}
