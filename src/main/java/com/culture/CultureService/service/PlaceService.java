package com.culture.CultureService.service;

import com.culture.CultureService.dto.PlaceDto;
import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private PlaceDto placeDto;

    public PlaceDto getPlaceDetail(Long id) {
        PlaceEntity placeEntity = placeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 ID를 가진 시설이 존재하지 않습니다."));
        return placeDto.of(placeEntity);
    }

}
