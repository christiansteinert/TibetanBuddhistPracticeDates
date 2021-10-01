package de.christian_steinert.practice_dates.practicedates.controllers;


import de.christian_steinert.practice_dates.practicedates.calendars_service.CalendarInfo;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface CalendarInfoMapper {
    CalendarInfoMapper INSTANCE = Mappers.getMapper(CalendarInfoMapper.class);

    CalendarInfoDto convert(CalendarInfo entity);
}
