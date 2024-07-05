package com.culture.CultureService.repository;

import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Long> {

    // 특별한 메서드가 필요하면 추가할 수 있음

}
