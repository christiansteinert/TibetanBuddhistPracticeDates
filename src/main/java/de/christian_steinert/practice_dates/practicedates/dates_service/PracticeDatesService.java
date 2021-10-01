package de.christian_steinert.practice_dates.practicedates.dates_service;

import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo.PracticeType;
import de.christian_steinert.practice_dates.practicedates.dates_service.tibetan_calendar_conversion.TibDate;
import de.christian_steinert.practice_dates.practicedates.dates_service.tibetan_calendar_conversion.TibetanDateCalculation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PracticeDatesService {
    TibetanDateCalculation dateCalc;

    public PracticeDatesService() {
        dateCalc = new TibetanDateCalculation();
    }

    Date toDateObj(LocalDate d) {
        var date = java.util.Date.from(d.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return date;
    }

    public List<DayInfo> getMonthInfo(int year, int month) {
        var from = LocalDate.of(year, month, 1);
        var to = LocalDate.of(year, month, from.lengthOfMonth());


        return getDateRangeInfo(from, to);
    }

    public List<DayInfo> getDateRangeInfo(LocalDate from, LocalDate to) {
        var result = new ArrayList<DayInfo>();
        var date = from;

        while (!date.isAfter(to)) {

            var tibDate = dateCalc.getTibetanDateForGregorianDate(toDateObj(date));
            var nextTibDate = dateCalc.getTibetanDateForGregorianDate(toDateObj(date.plusDays(1)));
            var practices = getPractices(tibDate, false);

            if (nextTibDate.tibDay - tibDate.tibDay == 2) {
                // the next Tibetan day number will be skipped.
                // all practices for that day number should be done on the current day.
                var skippedTibDate = new TibDate(tibDate.rabjung, tibDate.tibYear, tibDate.tibMonth, tibDate.monthFlag, tibDate.tibDay + 1);
                skippedTibDate.isSkippedDay = true;
                skippedTibDate.doubleDayFlag = 0;
                practices.addAll(getPractices(skippedTibDate, true));
            }

            var dateInfo = new DayInfo(date, tibDate.tibMonth, tibDate.tibDay,
                    tibDate.doubleDayFlag, tibDate.monthFlag, practices);

            result.add(dateInfo);
            date = date.plusDays(1);
        }

        return result;
    }

    private List<PracticeInfo> getPractices(TibDate tibDate, boolean isSkippedDay) {
        var result = new ArrayList<PracticeInfo>();

        if (tibDate.doubleDayFlag != 1) {
            // on doubled days practices are usually done on the second day so we skip the first date of a double day.

            /**** BUDDHA DAYS *****/
            if (tibDate.monthFlag != 2) {
                // in doubled months, buddha days seem to be in the first of the two months

                if (tibDate.tibMonth == 1 && tibDate.tibDay == 1) {
                    result.add(new PracticeInfo(PracticeType.OTHER, "Tibetan New Year", "Beginning of new Tibetan year. During the first fifteen days, fifteen 'Miracle Days' are celebrated which commemorate fifteen miracles that the Buddha is said to have performed."));
                }

                if (tibDate.tibMonth == 1 && tibDate.tibDay == 15) {
                    // 15th of 1st Tibetan month -> Day of Miracles
                    result.add(new PracticeInfo(PracticeType.BUDDHA_DAY, "Day of Miracles", "Day of Miracles (Chötrul Düchen). The fifteenth day of a new Tibetan year is the the culmination of the fifteen 'Miracle Days' which commemorate fifteen miracles that the Buddha is said to have performed. The Day of Miracles is one of four important Tibetan holidays related to the Buddha."));
                }

                if (tibDate.tibMonth == 4 && tibDate.tibDay == 15) {
                    // 15th of 4th Tibetan month -> Saka Dawa
                    result.add(new PracticeInfo(PracticeType.BUDDHA_DAY, "Buddha's Birth, Enlightenment and Paranirvana", "Celebration of the Buddha's birth, enlightenment and paranirvana (Saka Dawa Düchen). Saka Dawa is one of four important Tibetan holidays related to the Buddha."));
                }

                if (tibDate.tibMonth == 6 && tibDate.tibDay == 4) {
                    // 4th of 6th Tibetan month -> Turning of the Wheel
                    result.add(new PracticeInfo(PracticeType.BUDDHA_DAY, "Buddha's first teaching", "Celebration of Buddha's first 'turning of the wheel of Dharma' (Chökhor Düchen). On this day the first teaching of the Buddha is celebrated. Chökhor Düchen is one of four important Tibetan holidays related to the Buddha."));
                    result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "Buddha days and eclipses are considered to be particularly powerful days for taking the Eight Mahayana Precepts."));
                }

                if (tibDate.tibMonth == 9 && tibDate.tibDay == 22) {
                    // 4th of 6th Tibetan month -> Descent from Tushita
                    result.add(new PracticeInfo(PracticeType.BUDDHA_DAY, "Buddha's descent from Tushita", "Celebration of Buddha's descent from the god realm of Tushita (Lhabab Düchen) after the Buddha had taught the Dharma to his mother there. Lhabap Düchen is one of four important Tibetan holidays related to the Buddha."));
                    result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "Buddha days and eclipses are considered to be particularly powerful days for taking the Eight Mahayana Precepts."));
                }
            }

            /**** regular practice days ****/
            String doubleMonthSpecialTsokWarning = "";
            if(tibDate.tibMonth >= 11) {
                if (tibDate.monthFlag == 1) {
                    doubleMonthSpecialTsokWarning = String.format(" NOTE: During this year the %dth Tibetan month is doubled. It is not clear if the special Tsog day is on this day or one lunar month later.", tibDate.tibMonth);
                } else if (tibDate.monthFlag == 2) {
                    doubleMonthSpecialTsokWarning = String.format(" NOTE: During this year the %dth Tibetan month is doubled. It is not clear if the special Tsog day is on this day or one lunar month earlier.", tibDate.tibMonth);
                }
            }

            switch (tibDate.tibDay) {
                case 8:
                    result.add(new PracticeInfo(PracticeType.TARA, "Tara", "The eighth day of each Tibetan lunar month is considered to be a particularly suitable day for performing Tara pujas."));
                    result.add(new PracticeInfo(PracticeType.MEDICINE_BUDDHA, "Medicine Buddha", "The eighth day of each Tibetan lunar month is considered to be a particularly suitable day for performing Medicine Buddha pujas."));
                    if (tibDate.tibMonth != 1 || tibDate.monthFlag == 2) {
                        result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "The eighth day of each Tibetan lunar month is considered to be a powerful day for taking the Eight Mahayana Precepts."));
                    }
                    break;
                case 10:
                    if(tibDate.tibMonth == 12) {
                        result.add(new PracticeInfo(PracticeType.TSOG, "Tsog (Special Heruka Tsog)", "Tsog offering (Daka Tsog). The tenth day of each Tibetan month is one of the two days each month when Tsog offerings should be performed. The Tsog offering on the 10th day of the 12th Tibetan month is considered to be one of the two most important Tsog days of the entire year. Lama Zopa Rinpoche explained: ' The Tibetan 12th month is a special time for father tantra, a special time for Chakrasamvara, and a special month to offer Chakrasamvara tsog.' Phabongkha Rinpoche writes: '...the twenty-fifth day of the eleventh month is the holy time of the Mother, and the tenth day of the twelfth month is the holy time of the Father'." + doubleMonthSpecialTsokWarning));
                    } else {
                        result.add(new PracticeInfo(PracticeType.TSOG, "Tsog", "Tsog offering (Daka Tsog). The tenth day of each Tibetan month is one of the two days each month when Tsog offerings should be performed."));
                    }
                    break;
                case 15:
                    result.add(new PracticeInfo(PracticeType.MOON, "Full Moon", "Full moon days are considered to be powerful days for positive practices."));
                    result.add(new PracticeInfo(PracticeType.MEDICINE_BUDDHA, "Medicine Buddha", "The fifteenth day of each Tibetan lunar month is considered to be a particularly suitable day for performing Medicine Buddha pujas."));
                    if (tibDate.tibMonth != 1 || tibDate.monthFlag == 2) {
                        result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "Full moon days are considered to be powerful days for taking the Eight Mahayana Precepts."));
                    }
                    break;
                case 25:
                    if(tibDate.tibMonth == 11) {
                        result.add(new PracticeInfo(PracticeType.TSOG, "Tsog (Special Vajayogini Tsog)", "Tsog offering (Dakini Tsog). The twenty-fifth day of each Tibetan month is one of the two days per month when Tsog offerings should be performed. The Tsog offering on the 25th day of the 11th Tibetan month is considered to be one of the two most important Tsog days of the entire year. Lama Zopa Rinpoche explained that 'The Tibetan 11th month (Gyal Dawa) is a special time for mother tantra, a special time for Vajrayogini, and a special month to offer Vajrayogini tsog.' Phabongkha Rinpoche writes: '...the twenty-fifth day of the eleventh month is the holy time of the Mother, and the tenth day of the twelfth month is the holy time of the Father'." + doubleMonthSpecialTsokWarning));
                    } else {
                        result.add(new PracticeInfo(PracticeType.TSOG, "Tsog", "Tsog offering (Dakini Tsog). The twenty-fifth day of each Tibetan month is one of the two days per month when Tsog offerings should be performed."));
                    }
                    break;
                case 29:
                    result.add(new PracticeInfo(PracticeType.PROTECTOR_PUJA, "Protector Puja", "The twenty-ninth of each Tibetan month day is considered to be beneficial for protector pujas."));
                    break;
                case 30:
                    result.add(new PracticeInfo(PracticeType.MOON, "New Moon", "New moon days are considered to be powerful days for positive practices."));
                    result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "New moon days are considered to be powerful days for taking the Eight Mahayana Precepts."));
                    break;
            }
        }

        if (tibDate.tibMonth == 1 && tibDate.monthFlag != 2 && tibDate.tibDay <= 15) {
            // during first 15 days of the Tibetan year all days are precept days
            // for day 15 it has already been added above
            result.add(new PracticeInfo(PracticeType.PRECEPTS, "Precepts", "Buddha days and eclipses are considered to be particularly powerful days for taking the Eight Mahayana Precepts."));
        }

        for (var resultItem : result) {
            String dayInfo;
            var dayNum = tibDate.tibDay;
            var dayTxt = "";
            if (isSkippedDay) {
                dayNum -= 1;
            }
            if (tibDate.doubleDayFlag == 2) {
                dayTxt = "Repeated day";
            } else {
                dayTxt = "Day";
            }

            if (tibDate.monthFlag != 2) {
                dayInfo = String.format("%s %d of Tibetan month %d: ", dayTxt, dayNum, tibDate.tibMonth);
            } else {
                dayInfo = String.format("%s %d of repeated Tibetan month %d: ", dayTxt, dayNum, tibDate.tibMonth);
            }

            if (isSkippedDay) {
                var remark = String.format(" Normally this event would be on the %dth day of the Tibetan month but in the current Tibetan month the day number %d is skipped so the event done on the previous Tibetan date.", tibDate.tibDay, tibDate.tibDay);
                resultItem.setDescription(dayInfo + resultItem.getDescription() + remark);
            } else {
                resultItem.setDescription(dayInfo + resultItem.getDescription());
            }
        }

        return result;
    }
}
