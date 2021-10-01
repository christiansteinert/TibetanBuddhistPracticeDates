package de.christian_steinert.practice_dates.practicedates.dates_service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PracticeInfo {
    public enum PracticeType{
        BUDDHA_DAY,
        MOON,
        TSOG,
        TARA,
        MEDICINE_BUDDHA,
        PRECEPTS,
        PROTECTOR_PUJA,
        OTHER
    }

    private PracticeType type;
    private String name;
    private String description;
}
