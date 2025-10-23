package com.mexazon.app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mexazon.app.dto.BusinessCard;
import com.mexazon.app.service.BusinessSearchService;

@RestController
@RequestMapping("/api/businesses")
public class BusinessSearchController {

    private final BusinessSearchService service;

    public BusinessSearchController(BusinessSearchService service) {
        this.service = service;
    }

    // Ej: /api/businesses/search?q=tacos&alcaldia=Benito%20Ju%C3%A1rez&categories=tacos,tamales&page=0&size=20&sort=rating,desc
    @GetMapping("/search")
    public Page<BusinessCard> search(
            @RequestParam(required = false, name = "q") String q,
            @RequestParam(required = false) String alcaldia,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "rating,desc") String sort
    ) {
        return service.search(q, alcaldia, categories, page, size, sort);
    }

    @GetMapping("/top")
    public Page<BusinessCard> top(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return service.topInUserAlcaldia(userId, page, size);
    }

}