package com.culture.CultureService.controller;

import com.culture.CultureService.service.PlaceService;
import org.springframework.stereotype.Controller;

@Controller
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

}