package de.christian_steinert.practice_dates.practicedates.calendars_service;

import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo.PracticeType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarInfo {
    PracticeType id;
    String title;
    String color;
    String bgColor;
    String colorName;
}
