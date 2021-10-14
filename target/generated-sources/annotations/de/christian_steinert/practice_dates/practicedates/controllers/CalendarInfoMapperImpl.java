package de.christian_steinert.practice_dates.practicedates.controllers;

import de.christian_steinert.practice_dates.practicedates.calendars_service.CalendarInfo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-10-14T23:35:40+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (GraalVM Community)"
)
public class CalendarInfoMapperImpl implements CalendarInfoMapper {

    @Override
    public CalendarInfoDto convert(CalendarInfo entity) {
        if ( entity == null ) {
            return null;
        }

        String id = null;
        String title = null;
        String color = null;
        String bgColor = null;

        if ( entity.getId() != null ) {
            id = entity.getId().name();
        }
        title = entity.getTitle();
        color = entity.getColor();
        bgColor = entity.getBgColor();

        CalendarInfoDto calendarInfoDto = new CalendarInfoDto( id, title, color, bgColor );

        return calendarInfoDto;
    }
}
