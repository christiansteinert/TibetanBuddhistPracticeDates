package de.christian_steinert.practice_dates.practicedates.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public final class PracticeInfoDto {
    private String type;
    private String name;
    private String description;
}
