package com.culture.CultureService.controller;

import com.culture.CultureService.dto.ShowDto;
import com.culture.CultureService.dto.ShowSearchDto;
import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.service.ShowService;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/show")
public class ShowController {
    private final ShowService showService;

    @GetMapping(value = {"", "/page={page}"})
    public String showList(ShowSearchDto showSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 20개
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 15);

        Page<ShowEntity> shows = showService.getShowListPage(showSearchDto, pageable);
        model.addAttribute("shows", shows);
        model.addAttribute("showSearchDto", showSearchDto);
        model.addAttribute("maxPage", 10);
        return "show/showList";
    }

    //공연상세페이지
    @GetMapping(value = "/id={id}")
    public String showDetail(@PathVariable("id") Long id, Model model) {
        try {
            ShowDto showDto = showService.getShowDetail(id);
            model.addAttribute("showDto",showDto);
            return "show/showDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 공연입니다.");
            return "show/showList";
        }
    }
    //공연상세페이지
    @GetMapping(value = "/showId={showId}")
    public String showDetail(@PathVariable("showId") String showId, Model model) {
        try {
            ShowDto showDto = showService.getShowDetail(showId);
            model.addAttribute("showDto",showDto);
            return "show/showDetail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 공연입니다.");
            return "show/showList";
        }
    }

}
