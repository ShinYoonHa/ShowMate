package com.culture.CultureService.controller;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.entity.GenreStatisticEntity;
import com.culture.CultureService.service.AwardService;
import com.culture.CultureService.service.GenreStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AwardService awardService;
    private final GenreStatisticService genreStatisticService;

    @GetMapping(value = "/")
    public String main(Model model) {
        List<AwardEntity> awards = awardService.getAllAwards();

        // 수상작 중 최대 10개 선택
        List<AwardEntity> topAwards = awards.stream().limit(10).collect(Collectors.toList());

        // 장르별 통계 데이터 가져오기
        List<GenreStatisticEntity> genreStatistics = genreStatisticService.searchGenreStatistics(null);

        // Chart.js 데이터를 위한 리스트 초기화
        List<String> genreLabels = genreStatistics.stream().map(GenreStatisticEntity::getGenre).collect(Collectors.toList());
        List<Integer> performanceCounts = genreStatistics.stream().map(GenreStatisticEntity::getPerformanceCount).collect(Collectors.toList());
        List<Double> totalTicketRevenues = genreStatistics.stream().map(GenreStatisticEntity::getTotalTicketRevenue).collect(Collectors.toList());

        // 모델에 데이터 추가
        model.addAttribute("topAwards", topAwards); // 추가된 부분
        model.addAttribute("genreStatistics", genreStatistics);
        model.addAttribute("genreLabels", genreLabels);
        model.addAttribute("performanceCounts", performanceCounts);
        model.addAttribute("totalTicketRevenues", totalTicketRevenues);

        return "main"; // main.html 템플릿 반환
    }
}
