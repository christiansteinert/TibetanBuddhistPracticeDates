package de.christian_steinert.practice_dates.practicedates;

import de.christian_steinert.practice_dates.practicedates.controllers.CalendarInfoMapperImpl;
import de.christian_steinert.practice_dates.practicedates.controllers.DayInfoMapperImpl;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// help Spring Native / graalVM to find generated mapstruct classes
@TypeHint(types = { DayInfoMapperImpl.class, CalendarInfoMapperImpl.class } )
@Configuration
public class WebConfig extends WebMvcAutoConfiguration implements WebMvcConfigurer {
}
