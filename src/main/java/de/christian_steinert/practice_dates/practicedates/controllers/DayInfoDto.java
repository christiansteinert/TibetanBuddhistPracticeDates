package de.christian_steinert.practice_dates.practicedates.controllers;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
public final class DayInfoDto {
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;

    private int tibMonth;
    private int tibDay;
    private int repeatedTibDay;
    private int repeatedTibMonth;

    private List<PracticeInfoDto> practices;
}
