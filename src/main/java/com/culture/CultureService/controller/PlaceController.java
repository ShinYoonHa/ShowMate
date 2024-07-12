package com.culture.CultureService.controller;

import com.culture.CultureService.dto.PlaceSearchDto;
import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.service.PlaceService;
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
@RequestMapping(value = "/place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping(value = {"", "/page={page}"})
    public String placeList(PlaceSearchDto placeSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        //page.isPresent() 값 있으면 page.get(), 없으면 0 반환. 페이지 당 사이즈 20개
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 20);

        Page<PlaceEntity> places = placeService.getPlaceListPage(placeSearchDto, pageable);
        model.addAttribute("places", places);
        model.addAttribute("placeSearchDto", placeSearchDto);
        model.addAttribute("maxPage", 20);
        return "place/placeList";


    }

}