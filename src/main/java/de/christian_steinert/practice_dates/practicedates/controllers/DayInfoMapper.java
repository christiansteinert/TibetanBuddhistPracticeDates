package de.christian_steinert.practice_dates.practicedates.controllers;


import de.christian_steinert.practice_dates.practicedates.dates_service.DayInfo;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface DayInfoMapper {
    DayInfoMapper INSTANCE = Mappers.getMapper(DayInfoMapper.class);

    DayInfoDto convert(DayInfo entity);
}
