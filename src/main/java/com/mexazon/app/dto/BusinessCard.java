package com.mexazon.app.dto;

public record BusinessCard(
        Long id,
        String name,
        String avatarUrl,
        Long reviewsCount,
        Double rating
) {}
