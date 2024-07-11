package com.culture.CultureService.repository;

import com.culture.CultureService.entity.GenreStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface GenreStatisticRepository extends JpaRepository<GenreStatisticEntity, Long>, GenreStatisticRepositoryCustom{
    GenreStatisticEntity findByGenre(String genre);
    boolean existsByGenre(String genre);


}
