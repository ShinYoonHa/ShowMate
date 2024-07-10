package com.culture.CultureService.repository;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.entity.ShowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AwardRepositoryCustom {
    public Page<AwardEntity> getAwardListPage(AwardSearchDto awardSearchDto, Pageable pageable);
}
