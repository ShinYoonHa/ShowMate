package com.culture.CultureService.controller;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.entity.GenreStatisticEntity;
import com.culture.CultureService.service.AwardService;
import com.culture.CultureService.service.GenreStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AwardService awardService;
    private final GenreStatisticService genreStatisticService;

    @GetMapping(value = "/")
    public String main(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 12); // 페이지당 4개 표시
        AwardSearchDto awardSearchDto = new AwardSearchDto(); // 기본 검색 DTO 생성
        Page<AwardEntity> awards = awardService.getAwardListPage(awardSearchDto, pageable);

        // 장르별 통계 데이터 가져오기
        List<GenreStatisticEntity> genreStatistics = genreStatisticService.searchGenreStatistics(null);

        // Chart.js 데이터를 위한 리스트 초기화
        List<String> genreLabels = genreStatistics.stream().map(GenreStatisticEntity::getGenre).collect(Collectors.toList());
        List<Integer> performanceCounts = genreStatistics.stream().map(GenreStatisticEntity::getPerformanceCount).collect(Collectors.toList());
        List<Double> totalTicketRevenues = genreStatistics.stream().map(GenreStatisticEntity::getTotalTicketRevenue).collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("awards", awards);
        model.addAttribute("maxPage", 5); // 페이지네이션을 위한 최대 페이지 수 설정
        model.addAttribute("genreStatistics", genreStatistics);
        model.addAttribute("genreLabels", genreLabels);
        model.addAttribute("performanceCounts", performanceCounts);
        model.addAttribute("totalTicketRevenues", totalTicketRevenues);

        return "main"; // main.html 템플릿 반환
    }
}
