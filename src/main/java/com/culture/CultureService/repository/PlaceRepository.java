package com.culture.CultureService.repository;

import com.culture.CultureService.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long>, PlaceRepositoryCustom {
    PlaceEntity findByPlaceId(String placeId);
    boolean existsByPlaceId(String placeId);
}
