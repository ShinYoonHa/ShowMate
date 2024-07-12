package com.culture.CultureService.service;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.repository.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor  //final, @NonNull 붙은 변수 자동 주입
public class AwardService {
    private final AwardRepository awardRepository; //자동 주입

    //수상 목록 페이지 조회
    @Transactional(readOnly = true) //조회용 쿼리임을 명시
    public Page<AwardEntity> getAwardListPage(AwardSearchDto awardSearchDto, Pageable pageable) {
        return awardRepository.getAwardListPage(awardSearchDto, pageable);
    }

    // 모든 수상작 목록 조회
    @Transactional(readOnly = true)
    public List<AwardEntity> getAllAwards() {
        return awardRepository.findAll();
    }
}
