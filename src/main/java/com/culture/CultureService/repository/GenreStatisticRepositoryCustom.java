package com.culture.CultureService.repository;

import com.culture.CultureService.entity.GenreStatisticEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreStatisticRepositoryCustom {
    List<GenreStatisticEntity> searchGenreStatistics(String genre);
}
