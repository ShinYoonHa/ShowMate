package com.culture.CultureService.repository;

import com.culture.CultureService.dto.PlaceSearchDto;
import com.culture.CultureService.entity.PlaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaceRepositoryCustom {
    public Page<PlaceEntity> getPlaceListPage(PlaceSearchDto placeSearchDto, Pageable pageable);
}
