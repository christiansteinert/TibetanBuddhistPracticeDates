package de.christian_steinert.practice_dates.practicedates.ical_service;

import de.christian_steinert.practice_dates.practicedates.calendars_service.CalendarInfo;
import de.christian_steinert.practice_dates.practicedates.calendars_service.PracticeCalendarsService;
import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeDatesService;
import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IcalService {
    @Autowired
    private PracticeDatesService datesService;

    @Autowired
    private PracticeCalendarsService calendarService;

    /**
     * Wrap the content of an .ics DESCRIPTION text.
     * Lines should not be longer than 74 characters per line (including the word 'DESCRIPTION').
     * This method will wrap the text as needed so that it does not become too long.
     * Subsequent lines need to start with a tab to be recognized as continuations of a previous line.
     * As with .ical in general, it is necessary to use CR+LF for line breaks
     * @param text
     * @return
     */
    String wrapIcalText(String text) {
        var result = WordUtils.wrap(text, 55);
        return result.replace("\n", "\r\n\t ");
    }

    /**
     * Get the calendar information for a particular calendar ID
     * @param calendars list of calendar information objects
     * @param practiceType ID of the requested calendar object
     * @return
     */
    CalendarInfo getCalendar(List<CalendarInfo> calendars, PracticeInfo.PracticeType practiceType) {

        for (var calendar : calendars) {
            if (calendar.getId().equals(practiceType)) {
                return calendar;
            }
        }

        //fallback: return the last calendar ("others")
        return calendars.get(calendars.size() - 1);

    }

    /**
     * Get a String containing an .ics calendar file which contains
     *
     * @param request HTTP request object which triggered the current request. This object is used to determine the URL
     *                of the current application.
     * @param practiceTypes List of calendar ID / practice IDs whose content should be included in the .ics calendar data
     * @return A string containing iCal calendar data for an .ics file
     */
    public String getPracticeDatesAsIcal(HttpServletRequest request, ArrayList<PracticeInfo.PracticeType> practiceTypes) {
        var result = new StringBuilder();

        var calendarNames = practiceTypes.stream().map(x -> x.toString()).collect(Collectors.toList());
        var calendarNamesStr = String.join(",",  calendarNames);

        var startDate = LocalDate.now();  //LocalDate.of(year, 1, 1);
        var endDate = LocalDate.of(startDate.getYear() + 1, 12, 31);

        var calendars = calendarService.getCalendars();
        var dates = datesService.getDateRangeInfo(startDate, endDate);

        result.append("BEGIN:VCALENDAR\r\n");
        result.append("VERSION:2.0\r\n");
        result.append("PRODID:practicedates.christian-steinert.de\r\n");
        result.append("X-WR-CALNAME:Practice Dates\r\n");
        result.append("REFRESH-INTERVAL;VALUE=DURATION:PT168H\r\n");
        result.append("METHOD:PUBLISH\r\n");
        result.append(String.format("URL:%s://%s:%s/ical?calendars=%s\r\n", request.getScheme(), request.getServerName(), request.getServerPort(), calendarNamesStr));

        for (var date : dates) {
            for (var practice : date.getPractices()) {
                if (!practiceTypes.contains(practice.getType())) {
                    // skip any practices that were not selected by the user
                    continue;
                }

                var cal = getCalendar(calendars, practice.getType());
                var followingDay = LocalDate.of(date.getYear(), date.getMonth(), date.getDay()).plusDays(1);

                result.append("BEGIN:VEVENT\r\n");
                result.append("CLASS:PUBLIC\r\n");
                result.append(String.format("UID:%04d%02d%02d_%s@practicedates.christian-steinert.de\r\n", date.getYear(), date.getMonth(), date.getDay(), practice.getType()));
                result.append(String.format("CREATED:%04d0101T000000Z\r\n", date.getYear()));
                result.append(String.format("DTSTART;VALUE=DATE:%04d%02d%02d\r\n", date.getYear(), date.getMonth(), date.getDay()));
                result.append(String.format("DTEND;VALUE=DATE:%04d%02d%02d\r\n", followingDay.getYear(), followingDay.getMonth().getValue(), followingDay.getDayOfMonth()));
                result.append(String.format("DTSTAMP:%04d0101T000000Z\r\n", date.getYear()));
                result.append(String.format("LAST-MODIFIED:%04d0101T000000Z\r\n", date.getYear()));
                result.append(String.format("SUMMARY:%s\r\n", practice.getName()));
                result.append(String.format("DESCRIPTION:%s\r\n", wrapIcalText(practice.getDescription())));
                result.append("TRANSP:TRANSPARENT\r\n");
                result.append("X-MICROSOFT-CDO-BUSYSTATUS:FREE\r\n");
                result.append("X-MICROSOFT-CDO-ALLDAYEVENT:TRUE\r\n");
                result.append("X-MICROSOFT-MSNCALENDAR-ALLDAYEVENT:TRUE:FREE\r\n");

                result.append(String.format("X-APPLE-CALENDAR-COLOR:%s\r\n", cal.getBgColor()));
                result.append(String.format("COLOR:%s\r\n", cal.getColorName()));

                result.append("END:VEVENT\r\n");
            }
        }
        result.append("END:VCALENDAR\r\n");

        return result.toString();

    }
}
