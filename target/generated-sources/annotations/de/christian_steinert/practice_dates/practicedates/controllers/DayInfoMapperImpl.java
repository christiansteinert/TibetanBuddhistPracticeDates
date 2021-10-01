package de.christian_steinert.practice_dates.practicedates.controllers;

import de.christian_steinert.practice_dates.practicedates.dates_service.DayInfo;
import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-09-30T11:30:53+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.12 (GraalVM Community)"
)
public class DayInfoMapperImpl implements DayInfoMapper {

    @Override
    public DayInfoDto convert(DayInfo entity) {
        if ( entity == null ) {
            return null;
        }

        int year = 0;
        int month = 0;
        int day = 0;
        int dayOfWeek = 0;
        int tibMonth = 0;
        int tibDay = 0;
        int repeatedTibDay = 0;
        int repeatedTibMonth = 0;
        List<PracticeInfoDto> practices = null;

        year = entity.getYear();
        month = entity.getMonth();
        day = entity.getDay();
        dayOfWeek = entity.getDayOfWeek();
        tibMonth = entity.getTibMonth();
        tibDay = entity.getTibDay();
        repeatedTibDay = entity.getRepeatedTibDay();
        repeatedTibMonth = entity.getRepeatedTibMonth();
        practices = practiceInfoListToPracticeInfoDtoList( entity.getPractices() );

        DayInfoDto dayInfoDto = new DayInfoDto( year, month, day, dayOfWeek, tibMonth, tibDay, repeatedTibDay, repeatedTibMonth, practices );

        return dayInfoDto;
    }

    protected PracticeInfoDto practiceInfoToPracticeInfoDto(PracticeInfo practiceInfo) {
        if ( practiceInfo == null ) {
            return null;
        }

        String type = null;
        String name = null;
        String description = null;

        if ( practiceInfo.getType() != null ) {
            type = practiceInfo.getType().name();
        }
        name = practiceInfo.getName();
        description = practiceInfo.getDescription();

        PracticeInfoDto practiceInfoDto = new PracticeInfoDto( type, name, description );

        return practiceInfoDto;
    }

    protected List<PracticeInfoDto> practiceInfoListToPracticeInfoDtoList(List<PracticeInfo> list) {
        if ( list == null ) {
            return null;
        }

        List<PracticeInfoDto> list1 = new ArrayList<PracticeInfoDto>( list.size() );
        for ( PracticeInfo practiceInfo : list ) {
            list1.add( practiceInfoToPracticeInfoDto( practiceInfo ) );
        }

        return list1;
    }
}
