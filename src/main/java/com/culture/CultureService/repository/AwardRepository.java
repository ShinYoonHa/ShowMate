package com.culture.CultureService.repository;

import com.culture.CultureService.entity.AwardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<AwardEntity, Long>, AwardRepositoryCustom {
    AwardEntity findByAwardId(String showId);
    boolean existsByAwardId(String ShowId);
}