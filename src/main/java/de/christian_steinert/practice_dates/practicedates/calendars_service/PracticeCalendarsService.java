package de.christian_steinert.practice_dates.practicedates.calendars_service;

import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo.PracticeType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PracticeCalendarsService {

  public List<CalendarInfo> getCalendars() {
      var result = new ArrayList<CalendarInfo>();
      result.add(new CalendarInfo(PracticeType.BUDDHA_DAY, "Buddha Days", "#ffffff", "#c06000", "coral"));
      result.add(new CalendarInfo(PracticeType.MOON, "Full and New Moon", "#ffffff", "#000000", "black"));
      result.add(new CalendarInfo(PracticeType.TSOG, "Tsog", "#ffffff", "#7a0000", "firebrick"));
      result.add(new CalendarInfo(PracticeType.TARA, "Tara", "#ffffff", "#258d25", "forestgreen"));
      result.add(new CalendarInfo(PracticeType.MEDICINE_BUDDHA, "Medicine Buddha", "#ffffff", "#3061cd", "cornflowerblue"));
      result.add(new CalendarInfo(PracticeType.PRECEPTS, "Precepts", "#ffffff", "#a50000", "brown"));
//      result.add(new CalendarInfo(PracticeType.SOJONG, "Sojong", "#ffffff", "#dd7000", "Darkorange"));
      result.add(new CalendarInfo(PracticeType.PROTECTOR_PUJA, "Protector Puja", "#ffffff", "#490071","darkmagenta"));
      result.add(new CalendarInfo(PracticeType.OTHER, "Other", "#ffffff", "#666666", "dimgrey"));

      return result;
  }
}
