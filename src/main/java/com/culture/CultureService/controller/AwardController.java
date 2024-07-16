package com.culture.CultureService.controller;

import com.culture.CultureService.dto.AwardSearchDto;
import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.service.AwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/award")
public class AwardController {
    private final AwardService awardService;

    @GetMapping(value = {"", "/page={page}"})
    public String awardList(AwardSearchDto awardSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 15개
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 15);

        Page<AwardEntity> awards = awardService.getAwardListPage(awardSearchDto, pageable);
        model.addAttribute("awards", awards);
        model.addAttribute("awardSearchDto", awardSearchDto);
        model.addAttribute("maxPage", 10);
        return "award/awardList";
    }
}
