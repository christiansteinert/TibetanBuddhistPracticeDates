package de.christian_steinert.practice_dates.practicedates.controllers;

import de.christian_steinert.practice_dates.practicedates.calendars_service.PracticeCalendarsService;
import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeDatesService;
import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo.PracticeType;
import de.christian_steinert.practice_dates.practicedates.ical_service.IcalService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
public class TibDatesController {
    private final DayInfoMapper dayInfoMapper = Mappers.getMapper(DayInfoMapper.class);
    private final CalendarInfoMapper calendarInfoMapper = Mappers.getMapper(CalendarInfoMapper.class);

    @Autowired
    private PracticeDatesService datesService;

    @Autowired
    private PracticeCalendarsService calendarService;

    @Autowired
    private IcalService icalService;

    @GetMapping("/dates/{year}/{month}")
    public List<DayInfoDto> getMonthInfo(@PathVariable int year, @PathVariable int month) {
        var result = new ArrayList<DayInfoDto>();

        var dayInfos = datesService.getMonthInfo(year, month);
        for (var dayInfo : dayInfos) {
            result.add(dayInfoMapper.convert(dayInfo));
        }
        return result;
    }

    @GetMapping("/dates")
    public List<DayInfoDto> getDateRangeInfo(@DateTimeFormat(pattern = "yyyyMMdd") @RequestParam(required = true) LocalDate from,
                                             @DateTimeFormat(pattern = "yyyyMMdd") @RequestParam(required = true) LocalDate to) {

        return getDateRangeInfo2(from, to);
    }

    @GetMapping("/dates/{from}-{to}")
    public List<DayInfoDto> getDateRangeInfo2(@DateTimeFormat(pattern = "yyyyMMdd") @PathVariable LocalDate from,
                                              @DateTimeFormat(pattern = "yyyyMMdd") @PathVariable LocalDate to) {
        var result = new ArrayList<DayInfoDto>();

        var dayInfos = datesService.getDateRangeInfo(from, to);
        for (var dayInfo : dayInfos) {
            result.add(dayInfoMapper.convert(dayInfo));
        }
        return result;
    }

    @GetMapping("/calendars")
    public List<CalendarInfoDto> getCalendarInfo() {
        var result = new ArrayList<CalendarInfoDto>();

        var calendarInfos = calendarService.getCalendars();
        for (var calendarInfo : calendarInfos) {
            result.add(calendarInfoMapper.convert(calendarInfo));
        }
        return result;
    }


    @GetMapping("/ical")
    public ResponseEntity<ByteArrayResource> getIcal(@RequestParam(required = false) String calendars, HttpServletRequest request) {
        var practiceTypes = new ArrayList<PracticeType>();

        if (calendars != null) {
            var calNames = calendars.split(",");
            for (var practiceName : PracticeType.values()) {
                for (var calName : calNames) {
                    if (calName.equals(practiceName.toString())) {
                        practiceTypes.add(practiceName);
                    }
                }
            }
        }
        if (practiceTypes.isEmpty()) {
            for (var practiceName : PracticeType.values()) {
                practiceTypes.add(practiceName);
            }
        }

        String result = icalService.getPracticeDatesAsIcal(request, practiceTypes);

        byte[] resultBytes = result.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(resultBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=practice_dates.ics")
                .contentType(MediaType.valueOf("text/calendar; charset=\"utf-8\";"))
                .contentLength(resultBytes.length)
                .body(resource);
    }


}
