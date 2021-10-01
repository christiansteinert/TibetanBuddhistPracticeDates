package de.christian_steinert.practice_dates.practicedates.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarInfoDto {
    String id;
    String title;
    String color;
    String bgColor;
}
