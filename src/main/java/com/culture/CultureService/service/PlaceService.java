package com.culture.CultureService.service;

import com.culture.CultureService.dto.PlaceSearchDto;
import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    //수상 목록 페이지 조회
    @Transactional(readOnly = true) //조회용 쿼리임을 명시
    public Page<PlaceEntity> getPlaceListPage(PlaceSearchDto placeSearchDto, Pageable pageable) {
        return placeRepository.getPlaceListPage(placeSearchDto, pageable);
    }

}
