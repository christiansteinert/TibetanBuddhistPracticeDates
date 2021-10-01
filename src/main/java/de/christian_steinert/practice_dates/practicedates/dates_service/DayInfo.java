package de.christian_steinert.practice_dates.practicedates.dates_service;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Data
public final class DayInfo {
  private LocalDate date;

  private int tibMonth;

  private int tibDay;

  private int repeatedTibDay;

  private int repeatedTibMonth;

  public int getYear() {
      return date.getYear();
  }

  public int getMonth() {
    return date.getMonth().getValue();
  }

  public int getDay() {
    return date.getDayOfMonth();
  }

  public int getDayOfWeek() {
    return date.getDayOfWeek().getValue();
  }

  private List<PracticeInfo> practices;

}
