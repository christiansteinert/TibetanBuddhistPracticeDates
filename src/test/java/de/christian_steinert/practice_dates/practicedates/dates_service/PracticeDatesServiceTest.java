package de.christian_steinert.practice_dates.practicedates.dates_service;

import de.christian_steinert.practice_dates.practicedates.dates_service.PracticeInfo.PracticeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PracticeDatesServiceTest {
    PracticeDatesService objUnderTest;


    /**
     * Does getMonthInfo give the same result as getDateRangeInfo for all dates
     * of the respective period?
     */
    @Test
    void isGetMonthInfoEqualToDateRange() {

        var result = objUnderTest.getMonthInfo(2020, 2);

        LocalDate from = LocalDate.of(2020, 2, 1);
        LocalDate to = LocalDate.of(2020, 2, 29);
        var resultToCompare = objUnderTest.getDateRangeInfo(from, to);

        assertEquals(result,
                resultToCompare,
                "getMonthInfo should yield identical results to getDateRangeInfo for all days of the respective month");
    }

    /**
     * Helper method: Are the expected practices found on the a particular date?
     *
     * @param y                 year of the date to be checked
     * @param m                 month of the date to be checked
     * @param d                 day of the date to be checked
     * @param expectedPractices practices that are expected on this date
     */
    void checkForExpectedPractices(int y, int m, int d, PracticeType... expectedPractices) {
        var date = LocalDate.of(y, m, d);
        var result = objUnderTest.getDateRangeInfo(date, date);

        assertEquals(result.size(), 1, "There should be exactly one result when retrieving data for a single day.");

        var practices = result.get(0).getPractices();
        var practicesList = practices.stream().map(x -> x.getType()).collect(Collectors.toList());
        var expectedPracticesList = List.of(expectedPractices);

        // check if all the expected practices are found
        for (var expectedPractice : expectedPractices) {
            assertTrue(practicesList.contains(expectedPractice), "The day " + date + " does not contain the expected practice " + expectedPractice);
        }

        // check that no unexpected practices were generated
        for (var practice : practicesList) {
            assertTrue(expectedPracticesList.contains(practice), "The day " + date + " contains the unexpected practice " + practice);
        }

    }

    @Test
    /**
     * Does getDateRangeInfo for a set of days give the same result as
     * getDateRangeInfo for the respective period at once?
     */
    void isGetDateRangeInfoEqualToResultOfIndividualDays() {
        LocalDate from = LocalDate.of(2021, 12, 1);
        LocalDate to = LocalDate.of(2022, 1, 17);
        var result = objUnderTest.getDateRangeInfo(from, to);

        var currDate = from;
        var resultToCompare = new ArrayList<DayInfo>();
        while (!currDate.isAfter(to)) {
            var singleDayResult = objUnderTest.getDateRangeInfo(currDate, currDate);
            resultToCompare.addAll(singleDayResult);
            currDate = currDate.plusDays(1);
        }

        assertEquals(result,
                resultToCompare,
                "getDateRangeInfo for a period of days should yield the same result as for all of those days individually");
    }

    /**
     * Check if expected practices were determined correctly
     */
    @Test
    void checkPracticeData() {
        var TARA_MB_PRE = new PracticeType[]{PracticeType.TARA, PracticeType.MEDICINE_BUDDHA, PracticeType.PRECEPTS};
        var MOON_PRE = new PracticeType[]{PracticeType.MOON, PracticeType.PRECEPTS};
        var MOON_MB_PRE = new PracticeType[]{PracticeType.MOON, PracticeType.MEDICINE_BUDDHA, PracticeType.PRECEPTS};
        var TSOG = PracticeType.TSOG;
        var PROT = PracticeType.PROTECTOR_PUJA;
        var OTHER = PracticeType.OTHER;
        var PRE = PracticeType.PRECEPTS;

        /* check various dates to see if the generated result is as expected */

        /* December 2008 */
        checkForExpectedPractices(2008, 12, 1);
        checkForExpectedPractices(2008, 12, 2);
        checkForExpectedPractices(2008, 12, 3);
        checkForExpectedPractices(2008, 12, 4);
        checkForExpectedPractices(2008, 12, 5, TARA_MB_PRE);
        checkForExpectedPractices(2008, 12, 6);
        checkForExpectedPractices(2008, 12, 7, TSOG);
        checkForExpectedPractices(2008, 12, 8);
        checkForExpectedPractices(2008, 12, 9);
        checkForExpectedPractices(2008, 12, 10);
        checkForExpectedPractices(2008, 12, 11);
        checkForExpectedPractices(2008, 12, 12, MOON_MB_PRE);
        checkForExpectedPractices(2008, 12, 13);
        checkForExpectedPractices(2008, 12, 14);
        checkForExpectedPractices(2008, 12, 15);
        checkForExpectedPractices(2008, 12, 16);
        checkForExpectedPractices(2008, 12, 17);
        checkForExpectedPractices(2008, 12, 18);
        checkForExpectedPractices(2008, 12, 19);
        checkForExpectedPractices(2008, 12, 20);
        checkForExpectedPractices(2008, 12, 21, TSOG, OTHER); // Tsog and Tsonkhapa day
        checkForExpectedPractices(2008, 12, 22);
        checkForExpectedPractices(2008, 12, 23);
        checkForExpectedPractices(2008, 12, 24);
        checkForExpectedPractices(2008, 12, 25);
        checkForExpectedPractices(2008, 12, 26, PROT);
        checkForExpectedPractices(2008, 12, 27, MOON_PRE);
        checkForExpectedPractices(2008, 12, 28);
        checkForExpectedPractices(2008, 12, 29);
        checkForExpectedPractices(2008, 12, 30);
        checkForExpectedPractices(2008, 12, 31);

        /* December 2010 - February 2011 This period is interesting because it involves a doubled Tibetan month;
         *  it also contains doubled dates */
        checkForExpectedPractices(2010, 12, 1);
        checkForExpectedPractices(2010, 12, 2);
        checkForExpectedPractices(2010, 12, 3);
        checkForExpectedPractices(2010, 12, 4, PROT);
        checkForExpectedPractices(2010, 12, 5, MOON_PRE);
        checkForExpectedPractices(2010, 12, 6);
        checkForExpectedPractices(2010, 12, 7);
        checkForExpectedPractices(2010, 12, 8);
        checkForExpectedPractices(2010, 12, 9);
        checkForExpectedPractices(2010, 12, 10);
        checkForExpectedPractices(2010, 12, 11);
        checkForExpectedPractices(2010, 12, 12);
        checkForExpectedPractices(2010, 12, 13, TARA_MB_PRE);
        checkForExpectedPractices(2010, 12, 14);
        checkForExpectedPractices(2010, 12, 15);
        checkForExpectedPractices(2010, 12, 16, TSOG);
        checkForExpectedPractices(2010, 12, 17);
        checkForExpectedPractices(2010, 12, 18);
        checkForExpectedPractices(2010, 12, 19);
        checkForExpectedPractices(2010, 12, 20);
        checkForExpectedPractices(2010, 12, 21, MOON_MB_PRE);
        checkForExpectedPractices(2010, 12, 22);
        checkForExpectedPractices(2010, 12, 23);
        checkForExpectedPractices(2010, 12, 24);
        checkForExpectedPractices(2010, 12, 25);
        checkForExpectedPractices(2010, 12, 26);
        checkForExpectedPractices(2010, 12, 27);
        checkForExpectedPractices(2010, 12, 28);
        checkForExpectedPractices(2010, 12, 29);
        checkForExpectedPractices(2010, 12, 30, TSOG);
        checkForExpectedPractices(2010, 12, 31);

        checkForExpectedPractices(2011, 1, 1);
        checkForExpectedPractices(2011, 1, 2);
        checkForExpectedPractices(2011, 1, 3, PROT);
        checkForExpectedPractices(2011, 1, 4, MOON_PRE);
        checkForExpectedPractices(2011, 1, 5);
        checkForExpectedPractices(2011, 1, 6);
        checkForExpectedPractices(2011, 1, 7);
        checkForExpectedPractices(2011, 1, 8);
        checkForExpectedPractices(2011, 1, 9);
        checkForExpectedPractices(2011, 1, 10);
        checkForExpectedPractices(2011, 1, 11);
        checkForExpectedPractices(2011, 1, 12, TARA_MB_PRE);
        checkForExpectedPractices(2011, 1, 13);
        checkForExpectedPractices(2011, 1, 14, TSOG);
        checkForExpectedPractices(2011, 1, 15);
        checkForExpectedPractices(2011, 1, 16);
        checkForExpectedPractices(2011, 1, 17);
        checkForExpectedPractices(2011, 1, 18);
        checkForExpectedPractices(2011, 1, 19, MOON_MB_PRE);
        checkForExpectedPractices(2011, 1, 20);
        checkForExpectedPractices(2011, 1, 21);
        checkForExpectedPractices(2011, 1, 22);
        checkForExpectedPractices(2011, 1, 23);
        checkForExpectedPractices(2011, 1, 24);
        checkForExpectedPractices(2011, 1, 25);
        checkForExpectedPractices(2011, 1, 26);
        checkForExpectedPractices(2011, 1, 27);
        checkForExpectedPractices(2011, 1, 28, TSOG);
        checkForExpectedPractices(2011, 1, 29);
        checkForExpectedPractices(2011, 1, 30);
        checkForExpectedPractices(2011, 1, 31);

        checkForExpectedPractices(2011, 2, 1, PROT);
        checkForExpectedPractices(2011, 2, 2, MOON_PRE);
        checkForExpectedPractices(2011, 2, 3);
        checkForExpectedPractices(2011, 2, 4);
        checkForExpectedPractices(2011, 2, 5);
        checkForExpectedPractices(2011, 2, 6);
        checkForExpectedPractices(2011, 2, 7);
        checkForExpectedPractices(2011, 2, 8);
        checkForExpectedPractices(2011, 2, 9);
        checkForExpectedPractices(2011, 2, 10);
        checkForExpectedPractices(2011, 2, 11, TARA_MB_PRE);
        checkForExpectedPractices(2011, 2, 12);
        checkForExpectedPractices(2011, 2, 13, TSOG);
        checkForExpectedPractices(2011, 2, 14);
        checkForExpectedPractices(2011, 2, 15);
        checkForExpectedPractices(2011, 2, 16);
        checkForExpectedPractices(2011, 2, 17);
        checkForExpectedPractices(2011, 2, 18, MOON_MB_PRE);
        checkForExpectedPractices(2011, 2, 19);
        checkForExpectedPractices(2011, 2, 20);
        checkForExpectedPractices(2011, 2, 21);
        checkForExpectedPractices(2011, 2, 22);
        checkForExpectedPractices(2011, 2, 23);
        checkForExpectedPractices(2011, 2, 24);
        checkForExpectedPractices(2011, 2, 25);
        checkForExpectedPractices(2011, 2, 26);
        checkForExpectedPractices(2011, 2, 27, TSOG);
        checkForExpectedPractices(2011, 2, 28);

        /* March 2011: this period is interesting because of Tibetan new year */
        checkForExpectedPractices(2011, 3, 2);
        checkForExpectedPractices(2011, 3, 3, PROT);
        checkForExpectedPractices(2011, 3, 4, MOON_PRE);
        checkForExpectedPractices(2011, 3, 5, PRE, OTHER); // LOSAR
        checkForExpectedPractices(2011, 3, 9, PRE); // precepts up to 15th day, also on double day
        checkForExpectedPractices(2011, 3, 10, PRE); // precepts up to 15th day, also on double day
        checkForExpectedPractices(2011, 3, 13, TARA_MB_PRE);
        checkForExpectedPractices(2011, 3, 15, PRE, TSOG);
        checkForExpectedPractices(2011, 3, 18, PRE);
        checkForExpectedPractices(2011, 3, 19, PRE, PracticeType.BUDDHA_DAY, PracticeType.MOON, PracticeType.MEDICINE_BUDDHA); // day of miracles
        checkForExpectedPractices(2011, 3, 20);
        checkForExpectedPractices(2011, 3, 29, TSOG);


    }

    @BeforeEach
    void setUp() {
        objUnderTest = new PracticeDatesService();
    }
}