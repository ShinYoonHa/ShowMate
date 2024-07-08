package com.culture.CultureService.repository;

import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShowRepositoryCustom {
    public Page<ShowEntity> getShowListPage(ShowSearchDto showSearchDto, Pageable pageable);

}
