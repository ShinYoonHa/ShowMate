package com.culture.CultureService.service;

import com.culture.CultureService.dto.ShowDto;
import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.repository.ShowRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor  //final, @NonNull 붙은 변수 자동 주입
public class ShowService {
    private final ShowRepository showRepository; //자동 주입

    //공연 목록 페이지 조회
    @Transactional(readOnly = true) //조회용 쿼리임을 명시
    public Page<ShowEntity> getShowListPage(ShowSearchDto showSearchDto, Pageable pageable) {
        return showRepository.getShowListPage(showSearchDto, pageable);
    }
    public ShowDto getShowDetail(Long id) {
        ShowEntity showEntity = showRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        //ShowEntity -> ShowDto
        return ShowDto.of(showEntity);
    }
}
