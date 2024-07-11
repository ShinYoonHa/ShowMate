package com.culture.CultureService.service;

import com.culture.CultureService.entity.GenreStatisticEntity;
import com.culture.CultureService.repository.GenreStatisticRepository;
import com.culture.CultureService.repository.GenreStatisticRepositoryCustom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GenreStatisticService {

    private final GenreStatisticRepository genreStatisticRepository;
    private final GenreStatisticRepositoryCustom genreStatisticRepositoryCustom;
    private final GenreStatisticApiService genreStatisticsApiService;

    public void fetchAndSaveGenreStatistics(String stDate, String edDate) {
        try {
            genreStatisticsApiService.fetchAndSaveGenreStatistics(stDate, edDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public List<GenreStatisticEntity> searchGenreStatistics(String genre) {
        return genreStatisticRepositoryCustom.searchGenreStatistics(genre);
    }

    @Transactional(readOnly = true)
    public GenreStatisticEntity getGenreStatisticDetail(Long id) {
        return genreStatisticRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<GenreStatisticEntity> getAllGenreStatistics() {
        return genreStatisticRepository.findAll();
    }
}
