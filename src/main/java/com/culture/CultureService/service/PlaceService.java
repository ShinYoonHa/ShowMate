package com.culture.CultureService.service;

import com.culture.CultureService.dto.PlaceDto;
import com.culture.CultureService.dto.PlaceSearchDto;
import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    //수상 목록 페이지 조회
    @Transactional(readOnly = true) //조회용 쿼리임을 명시
    public Page<PlaceEntity> getPlaceListPage(PlaceSearchDto placeSearchDto, Pageable pageable) {
        return placeRepository.getPlaceListPage(placeSearchDto, pageable);
    }
    //공연장정보 페이지를 위한 PlaceDto 반환
    public PlaceDto getPlaceDetail(Long id) {
        PlaceEntity placeEntity = placeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 시설이 존재하지 않습니다."));
        // PlaceEntity를 PlaceDto로 변환
        PlaceDto placeDto = PlaceDto.of(placeEntity);
        return placeDto;
    }

}
