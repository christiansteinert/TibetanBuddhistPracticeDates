package de.christian_steinert.practice_dates.practicedates.dates_service.tibetan_calendar_conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A set of routines to convert back and forth between Gregorian and Tibetan
 * dates.
 * 
 * This class is self-contained and only contains the minimum amount of code
 * that is needed for doing date calculations.
 * 
 * There are no astrological calculations in this class.
 */
public class TibetanDateCalculation {
	/** first suported rabjung */
	public static final int RABJUNG_START = 1;

	/** last supported Rabjung */
	public static final int RABJUNG_END = 20;

	/**
	 * the offset by which the global month number must be adjusted in order to
	 * make the month 2 of year 1 in rabjung 16 the month number "0". This is
	 * required because many other calculations are interpolations that are
	 * based on this point in time and that are projecting forwards and
	 * backwards from this point
	 */
	private static final int C_ZLADAG_OFFSET = 11134;

	/**
	 * precalculated information with the most important characteristics of each
	 * Tibetan month
	 */
	static List<TibetanMonthInfo> tibetanMonthRecords = new ArrayList<TibetanMonthInfo>();

	/** internal record, representing the properties of a Tibetan month */
	private class TibetanMonthInfo {
		/** number of the 60-year cycle */
		int rabjung;

		/** number of the year within the 60-year cycle (1..60) */
		int tibYear;

		/** number of the month (1..12) */
		int tibMonth;

		/**
		 * type of Tibetan month - 0: normal month; 1: first month of a double
		 * month; 2: second month of a double month
		 */
		int monthFlag;

		/** total global number of this month */
		int zladag;

		/**
		 * number of the first skipped day during this month (or "0" if no day
		 * is skipped)
		 */
		int skip1;

		/**
		 * number of the second skipped day during this month (or "0" if no 2nd
		 * day is skipped)
		 */
		int skip2;

		/**
		 * number of the first doubled day during this month (or "0" if no day
		 * is doubled)
		 */
		int double1;

		/**
		 * number of the second doubled day during this month (or "0" if no 2nd
		 * day is doubled)
		 */
		int double2;

		/** the corresponding date for the first day of this Tibetan month */
		Date westernDate;

		/** Constructor */
		public TibetanMonthInfo(int rabjung, int tibYear, int tibMonth,
				int zladag, int monthFlag, int skip1, int skip2, int double1,
				int double2, Date westernDate) {
			super();
			this.rabjung = rabjung;
			this.tibYear = tibYear;
			this.tibMonth = tibMonth;
			this.zladag = zladag;
			this.monthFlag = monthFlag;
			this.skip1 = skip1;
			this.skip2 = skip2;
			this.double1 = double1;
			this.double2 = double2;
			this.westernDate = westernDate;
		}

		@Override
		public String toString() {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String date;

			if (this.westernDate != null) {
				date = dateFormat.format(this.westernDate);
			} else {
				date = "";
			}
			return this.rabjung + "\t" + this.tibYear + "\t" + this.tibMonth
					+ "\t" + this.monthFlag + "\t" + this.zladag + "\t"
					+ this.skip1 + "\t" + this.skip2 + "\t" + this.double1
					+ "\t" + this.double2 + "\t" + date + "\n";
		}
	}

	/** Default Constructor */
	public TibetanDateCalculation() {
		// do all pre-calculations and remember them in a static list for future use
		synchronized (tibetanMonthRecords) {
			if (tibetanMonthRecords.isEmpty()) {
				calculateNormalSystem();
				normalSystemAddSkippedAndDoubled();
				addWesternDates();
			}
		}
	}

	/**
	 * Get all matching gregorian dates for a given Tibetan date. Not all
	 * parameters need to be specified. If some parameters are have the value 0
	 * then all matching entries for all permutations of that value will be
	 * returned.
	 * 
	 * @param rabjung
	 *            the rabjung to be used or -1 if any rabjung is allowed
	 * @param tibYear
	 *            the year within the rabjung to be used or -1 if any year of
	 *            the rabjung is allowed
	 * @param tibMonth
	 *            the month within the Tibetan year to be used or -1 if any
	 *            month is allowed
	 * @param tibDay
	 *            the day within the Tibetan month to be used or -1 if any month
	 *            is allowed
	 * @return an Array with all matching Tibetan dates and their corresponding
	 *         Gregorian Dates
	 */
	public DatePair[] getGregorianDateForTibetanDate(int rabjung, int tibYear,
													 int tibMonth, int tibDay) {

		int startRab = rabjung > 0 ? rabjung : RABJUNG_START;
		int endRab = rabjung > 0 ? rabjung : RABJUNG_END;

		int startYear = tibYear > 0 ? tibYear : 1;
		int endYear = tibYear > 0 ? tibYear : 60;

		int startMonth = tibMonth > 0 ? tibMonth : 1;
		int endMonth = tibMonth > 0 ? tibMonth : 12;

		int startDay = tibDay > 0 ? tibDay : 1;
		int endDay = tibDay > 0 ? tibDay : 30;

		List<DatePair> result = new ArrayList<DatePair>();

		for (int rab = startRab; rab <= endRab; rab++) {
			for (int yr = startYear; yr <= endYear; yr++) {
				for (int mn = startMonth; mn <= endMonth; mn++) {
					for (int monthFlag = 0; monthFlag <= 2; monthFlag++) {
						TibetanMonthInfo rec = getRecord(rab, yr, mn, monthFlag);
						if (rec != null) {
							for (int day = startDay; day <= endDay; day++) {
								boolean skipped = ((day == rec.skip1) || (day == rec.skip2));
								boolean doubled = ((day == rec.double1) || (day == rec.double2));

								int dayDiff = day - 1;
								if ((day > rec.skip1) && (rec.skip1 != 0)) {
									dayDiff--;
								}
								if ((day > rec.skip2) && (rec.skip2 != 0)) {
									dayDiff--;
								}
								if ((day > rec.double1) && (rec.double1 != 0)) {
									dayDiff++;
								}
								if ((day > rec.double2) && (rec.double2 != 0)) {
									dayDiff++;
								}

								Date date = addDays(rec.westernDate, dayDiff);
								TibDate tibDate = new TibDate(rab, yr, mn,
										monthFlag, day);

								if (skipped) {
									tibDate.isSkippedDay = true;
									result.add(new DatePair(tibDate, null));
								} else if (doubled) {
									tibDate.doubleDayFlag = 1;
									result.add(new DatePair(tibDate, date));

									TibDate tibDate2 = new TibDate(rab, yr, mn,
											monthFlag, day);
									tibDate2.doubleDayFlag = 2;
									Date date2 = addDays(date, 1);
									result.add(new DatePair(tibDate2, date2));
								} else {
									result.add(new DatePair(tibDate, date));
								}
							}
						}
					}
				}
			}
		}
		return result.toArray(new DatePair[result.size()]);
	}

	/**
	 * Add a number of days to a date
	 * 
	 * @param date
	 *            the date to which some days should be added
	 * @param number
	 *            the number of days that should be added
	 * @return the resultant date after the addition
	 */
	private Date addDays(Date date, int number) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, number);
		return calendar.getTime();
	}

	/**
	 * get a Tibetan date for a Gregorian date
	 * 
	 * @param d
	 * @return an Array with all matching dates for the given Tibetan date
	 *         information or NULL if no Tibetan date could be determined
	 *         because the Gregorian date is out of range
	 */
	public TibDate getTibetanDateForGregorianDate(Date d) {
		d = stripTime(d);

		for (int i = tibetanMonthRecords.size() - 1; i >= 0; i--) {
			TibetanMonthInfo rec = tibetanMonthRecords.get(i);
			if (rec.westernDate.compareTo(d) <= 0) {
				int dayDiff = getDifferenceInDays(d, rec.westernDate) + 1;
				int day = dayDiff;

				TibDate result = new TibDate(rec.rabjung, rec.tibYear,
						rec.tibMonth, rec.monthFlag, day);

				// correct for skipped days
				if ((result.tibDay >= rec.skip1) && (rec.skip1 != 0)) {
					result.tibDay++;
				}
				if ((result.tibDay >= rec.skip2) && (rec.skip2 != 0)) {
					result.tibDay++;
				}

				// correct for doubled days
				if ((result.tibDay == rec.double1)
						|| (result.tibDay == rec.double2)) {
					result.doubleDayFlag = 1;
				}

				if ((result.tibDay > rec.double1) && (rec.double1 != 0)) {
					result.tibDay--;
					if (result.tibDay == rec.double1) {
						result.doubleDayFlag = 2;
					}
				}
				if ((result.tibDay > rec.double2) && (rec.double2 != 0)) {
					result.tibDay--;
					if (result.tibDay == rec.double2) {
						result.doubleDayFlag = 2;
					}
				}

				return result;
			}
		}

		return null;
	}

	/**
	 * get the number of days that two dates are apart
	 * 
	 * @param date1
	 *            the first date
	 * @param date2
	 *            the second date
	 * @return the days of difference between date1 and date2
	 */
	public static int getDifferenceInDays(Date date1, Date date2) {
		Date earlierDate = null;
		Date laterDate = null;
		if (date1.compareTo(date2) < 0) {
			earlierDate = date1;
			laterDate = date2;
		} else if (date1.compareTo(date2) > 0) {
			earlierDate = date2;
			laterDate = date1;
		} else {
			return 0;
		}

		int difference = 0;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(earlierDate);
		while (laterDate.getTime() > calendar.getTime().getTime()) {
			calendar.add(Calendar.DATE, 1);
			difference++;
		}
		return difference;
	}

	/**
	 * remove the time portion of a Date, thereby setting the hours, minute,
	 * second and millisecond part of the date to zero
	 * 
	 * @param date
	 *            the Date to be modified
	 * @return a Date object for 0:00 at the same day
	 */
	public static Date stripTime(Date date) {
		if (date == null)
			return null;

		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		return cal.getTime();
	}

	/**
	 * add a month record to the set of precalculated information
	 */
	private void addMonthRecord(int rabjung, int tibMonth, int year, int a2a,
			int a2b, int mflg) {

		if (year == 0) {
			year = 60;
			rabjung -= 1;
		}

		// adjust the month number in such a way that the 2nd month
		// of rabjung 16 will be month 0 because the original program
		// with all its interpolations starts to calculate from there.
		// Months before that will have a negative month number.
		int zladag = tibetanMonthRecords.size() - C_ZLADAG_OFFSET;

		TibetanMonthInfo record = new TibetanMonthInfo(rabjung, year, tibMonth,
				zladag, mflg, 0, 0, 0, 0, null);
		tibetanMonthRecords.add(record);
	}

	/**
	 * return all pre-calculated data as a CSV string with tab separation; this
	 * is only used for debugging purposes
	 */
	String getPrecalculatedData() {
		StringBuilder result = new StringBuilder();

		result.append("RABJUNG\tYEAR\tMONTH\tDOUBLE_MONTH_FLAG\tZLADAG\tSKIP1\tSKIP2\tDOUBLE1\tDOUBLE2\n");
		for (TibetanMonthInfo rec : tibetanMonthRecords) {
			result.append(rec.toString());
		}

		return result.toString();
	}

	/**
	 * internal helper routine: pre-calculate the position of various
	 * astronomical bodies for the entire timespan in which the tool is working
	 */
	private void calculateNormalSystem() {
		final int F_FIRST = 1;
		final int F_SECOND = 2;
		int ii;
		int jj;
		int y_start = 1;
		int y_end = 60;
		int r;
		int y, y1, y2;
		int m, m1, m2;
		int a, a1, a1r, a1z, a1a;
		int b, b1r, b1z, b1a;

		int totalYears = 0;

		for (r = RABJUNG_START; r <= RABJUNG_END; r++) {
			for (y = y_start; y <= y_end; y++) {
				totalYears++;
				for (m = 1; m <= 12; m++) {
					if (m > 2) {
						m1 = m;
						y1 = totalYears - 901;
					} else {
						m1 = m + 12;
						y1 = totalYears - 902;
					}

					// [1] For zla-dag (corrected number of passed months)
					// Calculate for y=any Tibetan year (1 to 60), m=any
					// Tibetan month (1 to 12)
					a = ((12 * y1) + m1) - 3;
					a1 = ((2 * a) + 55) / 65;
					a1r = a1;
					a1z = ((2 * a) + 55) % 65;
					a1z = (65 + a1z) % 65; // adjust for negative numbers
					a1a = a + a1r;
					a1a = (65 + a1a) % 65; // adjust for negative numbers

					if ((a1z > 1) && (a1z < 48)) {
						addMonthRecord(r, m, y, a1a, a1z, 0);
					} else if ((a1z == 48) || (a1z == 49)) {
						addMonthRecord(r, m, y, a1a, a1z, F_FIRST);
					} else if ((a1z == 50) || (a1z == 51)) {
						if (m > 1) {
							addMonthRecord(r, (m - 1), y, a1a, a1z, F_SECOND);
						} else {
							addMonthRecord(r, 12, (y - 1), a1a, a1z, F_SECOND);
						}
					} else if (a1z > 51) {
						if (m > 1) {
							addMonthRecord(r, (m - 1), y, a1a, a1z, 0);
						} else {
							addMonthRecord(r, 12, (y - 1), a1a, a1z, 0);
						}
					} else if ((a1z == 0) || (a1z == 1)) {
						if (m > 1) {
							addMonthRecord(r, m - 1, y, a1a - 1, 0, 0);
							addMonthRecord(r, m, y, a1a, a1z, 0);
						} else {
							addMonthRecord(r, 12, (y - 1), (a1a - 1), 0, 0);
							addMonthRecord(r, m, y, a1a, a1z, 0);
						}
					} else {
						// error in 1st case statement
						assert false;
					}
				}
			}
		}
	}

	// [2] For the gza'-dhru (root lunar weekday for the month)
	// [B] Save: gza'-dhru (root lunar weekday for the month)=
	// a3[0] a3[1] a3[2] a3[3] a3[4]
	private static int[] calculate_a_gzadhru(int zladag) {
		while (zladag < 0) {
			// gzadhru calculations repeat every 39592 months
			// --> transpose the calculations for negative month numbers
			// into a positive month number at the same position of a later
			// cycle. In this way we avoid calculating with negative month
			// numbers and thereby yield the correct results
			zladag += 39592;
		}

		int[] a3 = new int[5];
		int a3er = ((480 * zladag) + 20) / 707;
		a3[4] = (((480 * zladag) + 20) % 707);
		int a3dr = (2 + a3er) / 6;
		a3[3] = ((2 + a3er) % 6);
		int a3cr = ((50 * zladag) + 53 + a3dr) / 60;
		a3[2] = (((50 * zladag) + 53 + a3dr) % 60);
		int a3br = ((31 * zladag) + 57 + a3cr) / 60;
		a3[1] = (((31 * zladag) + 57 + a3cr) % 60);
		int a3ar = (zladag + 6 + a3br) / 7;
		a3[0] = ((zladag + 6 + a3br) % 7);
		return a3;
	}

	// [3] For the nyi-dhru (root sun position for the month)
	// [B] Save: nyi-dhru (root sun position for the month)=
	// a4[0] a4[1] a4[2] a4[3] a4[4]
	private static int[] calculate_a_nyidhru(int zladag) {
		while (zladag < 0) {
			// nyidhru calculations repeat every 804 months
			// --> transpose the calculations for negative month numbers
			// into a positive month number at the same position of a later
			// cycle. In this way we avoid calculating with negative month
			// numbers and thereby yield the correct results
			zladag += 804;
		}

		int[] a4 = new int[5];
		int a4er = ((17 * zladag) + 32) / 67;
		a4[4] = (((17 * zladag) + 32) % 67);
		int a4dr = (zladag + 4 + a4er) / 6;
		a4[3] = ((zladag + 4 + a4er) % 6);
		int a4cr = ((58 * zladag) + 10 + a4dr) / 60;
		a4[2] = (((58 * zladag) + 10 + a4dr) % 60);
		int a4br = ((10 * zladag) + 9 + a4cr) / 60;
		a4[1] = (((10 * zladag) + 9 + a4cr) % 60);
		int a4ar = ((2 * zladag) + 25 + a4br) / 27;
		a4[0] = (((2 * zladag) + 25 + a4br) % 27);

		return a4;
	}

	// [4] For ril-cha (root position in lunation cycle for the
	// month)
	// [B] Save: ril-cha (root position in lunation cycle for the
	// month)= a5[0] a5[1]
	private static int[] calculate_a_rilcha(int zladag) {
		while (zladag < 0) {
			// rilcha calculations repeat every 3528 months
			// --> transpose the calculations for negative month numbers
			// into a positive month number at the same position of a later
			// cycle. In this way we avoid calculating with negative month
			// numbers and thereby yield the correct results
			zladag += 3528;
		}

		int[] a5 = new int[2];
		int a5br = (zladag + 103) / 126;
		a5[1] = ((zladag + 103) % 126);

		int a5ar = ((2 * zladag) + 13 + a5br) / 28;
		a5[0] = (((2 * zladag) + 13 + a5br) % 28);

		return a5;
	}

	/**
	 * calculate skipped and double days to the pre-calculated information
	 */
	private void normalSystemAddSkippedAndDoubled() {
		TibetanMonthInfo record = tibetanMonthRecords.get(0);

		int[] a3 = calculate_a_gzadhru(record.zladag);
		int[] a4 = calculate_a_nyidhru(record.zladag);
		int[] a5 = calculate_a_rilcha(record.zladag);

		int prev_a13 = calca13a(30, a3, a4, a5);

		int recordNumber = tibetanMonthRecords.size();
		for (int i = 1; i < recordNumber; i++) {
			record = tibetanMonthRecords.get(i);
			a3 = calculate_a_gzadhru(record.zladag);
			a4 = calculate_a_nyidhru(record.zladag);
			a5 = calculate_a_rilcha(record.zladag);

			int aSkip1 = 0;
			int aSkip2 = 0;
			int aDbl1 = 0;
			int aDbl2 = 0;
			for (int j = 1; j <= 30; j++) {
				int a13a = calca13a(j, a3, a4, a5);
				if (a13a == prev_a13) {
					if (aSkip1 == 0) {
						aSkip1 = j;
					} else if (aSkip2 == 0) {
						aSkip2 = j;
					} else {
						System.out
								.println("A_SKIP field filled up for month recno "
										+ i + " (" + record + ")");
					}
				} else if (prev_a13 < 5) {
					if (a13a == prev_a13 + 2) {
						if (aDbl1 == 0) {
							aDbl1 = j;
						} else if (aDbl2 == 0) {
							aDbl2 = j;
						} else {
							System.out
									.println("A_DBL field filled up for month recno "
											+ i + " (" + record + ")");
						}
					}
				} else if (prev_a13 > 4) {
					if (a13a == prev_a13 - 5) {
						if (aDbl1 == 0) {
							aDbl1 = j;
						} else if (aDbl2 == 0) {
							aDbl2 = j;
						} else {
							System.out
									.println("A_DBL field filled up for month recno "
											+ i);
						}
					}
				}
				prev_a13 = a13a;
			}
			record.skip1 = aSkip1;
			record.skip2 = aSkip2;
			record.double1 = aDbl1;
			record.double2 = aDbl2;
		}
	}

	/**
	 * add corresponding western date for the beginning of each Tibetan month to
	 * the pre-calculated information
	 */
	private void addWesternDates() {
		int recordCount = tibetanMonthRecords.size();
		int entryRownum = locateRecord(17, 2, 1, 0);
		assert entryRownum != -1;

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(1988, 1, 18);
		java.util.Date dFirst = stripTime(calendar.getTime());
		java.util.Date dPrev = dFirst;

		TibetanMonthInfo record = tibetanMonthRecords.get(entryRownum);
		record.westernDate = dPrev;

		int nCount = 30;
		int aSkip1 = record.skip1;
		int aSkip2 = record.skip2;
		int aDbl1 = record.double1;
		int aDbl2 = record.double2;

		if ((aSkip1 != 0) && (aDbl1 == 0)) {
			nCount--;
		} else if ((aSkip2 != 0) && (aDbl2 == 0)) {
			nCount--;
		} else if ((aSkip1 == 0) && (aDbl1 != 0)) {
			// error
			assert false;
		} else if ((aSkip2 == 0) && (aDbl2 != 0)) {
			// error
			assert false;
		}
		for (int i = entryRownum + 1; i < recordCount; i++) {
			calendar.setTime(dPrev);
			calendar.add(Calendar.DATE, nCount);
			dPrev = calendar.getTime();

			record = tibetanMonthRecords.get(i);
			record.westernDate = dPrev;

			nCount = 30;
			aSkip1 = record.skip1;
			aSkip2 = record.skip2;
			aDbl1 = record.double1;
			aDbl2 = record.double2;

			if ((aSkip1 != 0) && (aDbl1 == 0)) {
				nCount--;
			}
			if ((aSkip2 != 0) && (aDbl2 == 0)) {
				nCount--;
			} else if ((aSkip1 == 0) && (aDbl1 != 0)) {
				// error
				assert false;
			} else if ((aSkip2 == 0) && (aDbl2 != 0)) {
				// error
				assert false;
			}
		}
		dPrev = dFirst;
		for (int i = entryRownum - 1; i >= 0; i--) {
			nCount = 30;

			record = tibetanMonthRecords.get(i);
			aSkip1 = record.skip1;
			aSkip2 = record.skip2;
			aDbl1 = record.double1;
			aDbl2 = record.double2;

			if ((aSkip1 != 0) && (aDbl1 == 0)) {
				nCount--;
			} else if ((aSkip2 != 0) && (aDbl2 == 0)) {
				nCount--;
			} else if ((aSkip1 == 0) && (aDbl1 != 0)) {
				// error
				assert false;
			} else if ((aSkip2 == 0) && (aDbl2 != 0)) {
				// error
				assert false;
			}
			calendar.setTime(dPrev);
			calendar.add(Calendar.DATE, -nCount);
			dPrev = calendar.getTime();

			record.westernDate = dPrev;
		}

	}

	/**
	 * locate the pre-calculated month description record for a Tibetan month
	 * 
	 * @param rabjung
	 *            rabjung to search for
	 * @param tibYear
	 *            year to search for
	 * @param tibMonth
	 *            month to search for
	 * @param monthFlag
	 *            0: return the record if the month is a non-doubled month and
	 *            null if this month is a doubled month; 1: return the first
	 *            month of a double month and return null if no this month is no
	 *            double month; 2: return the second month of a double month and
	 *            return null if no this month is no double month;
	 * @return the record for the matching Tibetan date or null if no record
	 *         matches the given information
	 */
	private TibetanMonthInfo getRecord(int rabjung, int tibYear, int tibMonth,
			int monthFlag) {
		int recPos = locateRecord(rabjung, tibYear, tibMonth, monthFlag);

		if (recPos != -1) {
			return tibetanMonthRecords.get(recPos);
		}

		return null;
	}

	/**
	 * locate the position of the pre-calculated record for a Tibetan month
	 * 
	 * @param rabjung
	 *            rabjung to search for
	 * @param tibYear
	 *            year to search for
	 * @param tibMonth
	 *            month to search for
	 * @param monthFlag
	 *            0: return the record if the month is a non-doubled month and
	 *            -1 if this month is a doubled month; 1: return the first month
	 *            of a double month and return -1 if no this month is no double
	 *            month; 2: return the second month of a double month and return
	 *            -1 if no this month is no double month;
	 * @return the record number of the matching Tibetan date or -1 if no record
	 *         matches the given information
	 */
	private int locateRecord(int rabjung, int tibYear, int tibMonth,
			int monthFlag) {
		int recordCount = tibetanMonthRecords.size();
		for (int pos = recordCount - 1; pos >= 0; pos--) {
			TibetanMonthInfo rec = tibetanMonthRecords.get(pos);
			if ((rec.rabjung == rabjung) && (rec.tibYear == tibYear)
					&& (rec.tibMonth == tibMonth)
					&& (monthFlag == rec.monthFlag)) {
				return pos;
			}
		}

		return -1;
	}

	// [10] Print from each record:
	// r cycle y2 year m2 month
	// rnam-dag grub-dhru (pure full tenet system's root figures
	// for the month):
	// zla-dag (corrected number of passed months) = a2a a2b
	// gza'-dhru (root lunar weekday) = a3[1] a3[2] a3[3] a3[4] a3[5]
	// nyi-dhru (root sun position) = a4[1] a4[2] a4[3] a4[4] a4[5]
	// ril-cha (root position in lunation cycle) = a5[1] a5[2]
	// dpal-ldan byed-dhru (glorious precis system's root figures
	// for the month):
	// zla-dag (corrected number of passed months)[for r cycle
	// y3 year m3 month]= b2a b2b
	// gza'-dhru (root lunar weekday) = b3[1] b3[2] b3[3]
	// nyi-dhru (root sun position) = b4[1] b4[2] b4[3] b4[4] b4[5]
	// ril-cha (root position in lunation cycle) = b5[1] b5[2]
	private int calca13a(int d, int[] a3, int[] a4, int[] a5) {
		int a6er, a6ez, a6dr, a6dz, a6cr, a6cz, a6br, a6bz, a6ar, a6az;
		int a7er, a7ez, a7dr, a7dz, a7cr, a7cz, a7br, a7bz, a7ar, a7az;
		int a8er, a8ez, a8dr, a8dz, a8cr, a8cz, a8br, a8bz, a8ar, a8az;
		int a9er, a9ez, a9dr, a9dz, a9cr, a9cz, a9br, a9bz, a9ar, a9az;
		/* int[][] a10sm; */int a10r, a10z, a10s, a10m;
		int a10a2r, a10a2z, a10a3r, a10a3z, a10a4r, a10a4z, a10a5r, a10a5z;
		// LOCAL a10a2,a10a3,a10a4,a10a5
		int a10b2, a10b3, a10b4, a10b5;
		int a10c1, a10c2, a10c3, a10c4, a10c5;
		int a10d1, a10d2, a10d3, a10d4, a10d5;
		int a10e1, a10e2, a10e3, a10e4, a10e5;
		int a9az1, a9az2, a9bz1;
		int a11a1, a11a2, a11a3, a11a4, a11a5;
		int a11b1 = 0, a11b2 = 0, a11b3, a11b4, a11b5;
		int a11cr, a11cz; /* int[][] a11csm */
		int a11cs, a11cm;
		int a11d2, a11d3r, a11d3z, a11d4r, a11d4z, a11d5r, a11d5z;
		int a11e2r, a11e2z, a11e3r, a11e3z, a11e4r, a11e4z, a11e5r, a11e5z;
		int a11f2 = 0, a11f3 = 0, a11f4 = 0, a11f5 = 0;
		int a10e5ar, a10e5az;
		int a11g1, a11g2, a11g3, a11g4, a11g5;
		int a11h1, a11h2, a11h3, a11h4;
		int a11i1, a11i2, a11i3, a11i4, a11i5;
		int a11j1, a11j2, a11j3, a11j4;
		int ii, jj; /* int[][] aUnits; */
		/* int[] a12; int[] a13; int[][] aRetval; */
		/* int[] a9; */

		// a6: gza'-rtag (lunar weekday daily-motion constant)
		a6er = (16 * d) / 707;
		a6ez = (16 * d) % 707;
		a6dr = ((4 * d) + a6er) / 6;
		a6dz = ((4 * d) + a6er) % 6;
		a6cr = ((3 * d) + a6dr) / 60;
		a6cz = ((3 * d) + a6dr) % 60;
		a6br = ((59 * d) + a6cr) / 60;
		a6bz = ((59 * d) + a6cr) % 60;
		a6ar = a6br / 7;
		a6az = a6br % 7;

		// a7: gza'-bar (mean lunar weekday)
		a7er = (a3[4] + a6ez) / 707;
		a7ez = (a3[4] + a6ez) % 707;
		a7dr = (a3[3] + a6dz + a7er) / 6;
		a7dz = (a3[3] + a6dz + a7er) % 6;
		a7cr = (a3[2] + a6cz + a7dr) / 60;
		a7cz = (a3[2] + a6cz + a7dr) % 60;
		a7br = (a3[1] + a6bz + a7cr) / 60;
		a7bz = (a3[1] + a6bz + a7cr) % 60;
		a7ar = (a3[0] + a6az + a7br) / 7;
		a7az = (a3[0] + a6az + a7br) % 7;

		// a8: nyi-rtag (sun's daily-motion constant)
		a8er = (43 * d) / 67;
		a8ez = (43 * d) % 67;
		a8dr = ((5 * d) + a8er) / 6;
		a8dz = ((5 * d) + a8er) % 6;
		a8cr = ((21 * d) + a8dr) / 60;
		a8cz = ((21 * d) + a8dr) % 60;
		a8br = ((4 * d) + a8cr) / 60;
		a8bz = ((4 * d) + a8cr) % 60;
		a8ar = a8br / 27;
		a8az = a8br % 27;

		// a9: nyi-bar (mean sun position)
		a9er = (a4[4] + a8ez) / 67;
		a9ez = (a4[4] + a8ez) % 67;
		a9dr = (a4[3] + a8dz + a9er) / 6;
		a9dz = (a4[3] + a8dz + a9er) % 6;
		a9cr = (a4[2] + a8cz + a9dr) / 60;
		a9cz = (a4[2] + a8cz + a9dr) % 60;
		a9br = (a4[1] + a8bz + a9cr) / 60;
		a9bz = (a4[1] + a8bz + a9cr) % 60;
		a9ar = (a4[0] + a8az + a9br) / 27;
		a9az = (a4[0] + a8az + a9br) % 27;

		int[][] a10sm = { { 0, 5 }, { 5, 5 }, { 10, 5 }, { 15, 4 }, { 19, 3 },
				{ 22, 2 }, { 24, 1 }, { 25, 1 }, { 24, 2 }, { 22, 3 },
				{ 19, 4 }, { 15, 5 }, { 10, 5 }, { 5, 5 } };
		a10r = (a5[0] + d) / 14;
		a10z = (a5[0] + d) % 14;
		a10s = a10sm[a10z][0];
		a10m = a10sm[a10z][1];

		a10a2r = (a5[1] * a10m) / 126;
		a10a2z = (a5[1] * a10m) % 126;
		// a10a2 = (a5[1]*a10m)/126
		a10a3r = (60 * a10a2z) / 126;
		a10a3z = (60 * a10a2z) % 126;
		// a10a3 = (60*a10a2z)/126
		a10a4r = (6 * a10a3z) / 126;
		a10a4z = (6 * a10a3z) % 126;
		// a10a4 = (6*a10a3z)/126
		a10a5r = (707 * a10a4z) / 126;
		a10a5z = (707 * a10a4z) % 126;
		// a10a5 = (707*a10a4z)/126
		assert (a10a5z == 0);

		if ((a10z >= 0) && (a10z <= 6)) {
			a10b2 = a10s + a10a2r;
			a10b3 = a10a3r;
			a10b4 = a10a4r;
			a10b5 = a10a5r;
		} else {
			a10b2 = a10s - a10a2r - 1;
			a10b3 = 59 - a10a3r;
			a10b4 = 5 - a10a4r;
			a10b5 = 707 - a10a5r;
		}

		// a10e: gza'-phyed dag-pa (half-corrected lunar weekday)
		if (a10r % 2 == 0) {
			a10c1 = a7az;
			a10c2 = a7bz + a10b2;
			a10c3 = a7cz + a10b3;
			a10c4 = a7dz + a10b4;
			a10c5 = a7ez + a10b5;
			if (a10c5 >= 707) {
				a10e5 = a10c5 - 707;
				a10d4 = a10c4 + 1;
			} else {
				a10e5 = a10c5;
				a10d4 = a10c4;
			}
			if (a10d4 >= 6) {
				a10e4 = a10d4 - 6;
				a10d3 = a10c3 + 1;
			} else {
				a10e4 = a10d4;
				a10d3 = a10c3;
			}
			if (a10d3 >= 60) {
				a10e3 = a10d3 - 60;
				a10d2 = a10c2 + 1;
			} else {
				a10e3 = a10d3;
				a10d2 = a10c2;
			}
			if (a10d2 >= 60) {
				a10e2 = a10d2 - 60;
				a10d1 = a10c1 + 1;
			} else {
				a10e2 = a10d2;
				a10d1 = a10c1;
			}
			if (a10d1 >= 7) {
				a10e1 = a10d1 - 7;
			} else {
				a10e1 = a10d1;
			}
		} else {
			a10c1 = a7az;
			a10c2 = a7bz - a10b2;
			a10c3 = a7cz - a10b3;
			a10c4 = a7dz - a10b4;
			a10c5 = a7ez - a10b5;
			if (a10b2 > a7bz) {
				a10e1 = a10c1 - 1;
				a10d2 = 60 + a10c2;
			} else {
				a10e1 = a10c1;
				a10d2 = a10c2;
			}
			if (a10b3 > a7cz) {
				a10e2 = a10d2 - 1;
				a10d3 = 60 + a10c3;
			} else {
				a10e2 = a10d2;
				a10d3 = a10c3;
			}
			if (a10b4 > a7dz) {
				a10e3 = a10d3 - 1;
				a10d4 = 6 + a10c4;
			} else {
				a10e3 = a10d3;
				a10d4 = a10c4;
			}
			if (a10b5 > a7ez) {
				a10e4 = a10d4 - 1;
				a10e5 = 707 + a10c5;
			} else {
				a10e4 = a10d4;
				a10e5 = a10c5;
			}
		}

		if (a9az < 6) {
			a9az1 = a9az + 27;
		} else {
			a9az1 = a9az;
		}
		if (a9bz < 45) {
			a9az2 = a9az1 - 1;
			a9bz1 = a9bz + 60;
		} else {
			a9az2 = a9az1;
			a9bz1 = a9bz;
		}
		a11a1 = a9az2 - 6;
		a11a2 = a9bz1 - 45;
		a11a3 = a9cz;
		a11a4 = a9dz;
		a11a5 = a9ez;
		if ((a11a1 >= 13) && (a11a2 >= 30)) {
			a11b1 = a11a1 - 13;
			a11b2 = a11a2 - 30;
		}
		if ((a11a1 > 13) && (a11a2 < 30)) {
			a11b1 = a11a1 - 14;
			a11b2 = a11a2 + 30;
		}
		if (((a11a1 == 13) && (a11a2 < 30)) || (a11a1 < 13)) {
			a11b1 = a11a1;
			a11b2 = a11a2;
		}
		a11b3 = a11a3;
		a11b4 = a11a4;
		a11b5 = a11a5;
		a11cr = ((60 * a11b1) + a11b2) / 135;
		a11cz = ((60 * a11b1) + a11b2) % 135;
		int[][] a11csm = { { 0, 6 }, { 6, 4 }, { 10, 1 }, { 11, 1 }, { 10, 4 },
				{ 6, 6 } };
		// a11cs = a11csm[a11cr + 1,1];
		a11cs = a11csm[a11cr][0];
		// a11cm = a11csm[a11cr + 1,2];
		a11cm = a11csm[a11cr][1];
		a11d5r = (a9ez * a11cm) / 67;
		a11d5z = (a9ez * a11cm) % 67;
		a11d4r = ((a9dz * a11cm) + a11d5r) / 6;
		a11d4z = ((a9dz * a11cm) + a11d5r) % 6;
		a11d3r = ((a9cz * a11cm) + a11d4r) / 60;
		a11d3z = ((a9cz * a11cm) + a11d4r) % 60;
		a11d2 = (a11cz * a11cm) + a11d3r;
		a11e2r = a11d2 / 135;
		a11e2z = a11d2 % 135;
		a11e3r = ((60 * a11e2z) + a11d3z) / 135;
		a11e3z = ((60 * a11e2z) + a11d3z) % 135;
		a11e4r = ((6 * a11e3z) + a11d4z) / 135;
		a11e4z = ((6 * a11e3z) + a11d4z) % 135;
		a11e5r = ((67 * a11e4z) + a11d5z) / 135;
		a11e5z = ((67 * a11e4z) + a11d5z) % 135;
		assert a11e5z == 0;

		if ((a11cr >= 0) && (a11cr <= 2)) {
			a11f2 = a11cs + a11e2r;
			a11f3 = a11e3r;
			a11f4 = a11e4r;
			a11f5 = a11e5r;
		}
		if ((a11cr >= 3) && (a11cr <= 5)) {
			a11f2 = a11cs - a11e2r - 1;
			a11f3 = 59 - a11e3r;
			a11f4 = 5 - a11e4r;
			a11f5 = 67 - a11e5r;
		}

		// a12: nyi-dag (corrected sun position)
		// a13: gza'-dag (corrected lunar weekday)
		int[] a12 = new int[5];
		int[] a13 = new int[6];
		a10e5ar = (67 * a10e5) / 707;
		a10e5az = (67 * a10e5) % 707;

		if (((a11a1 >= 13) && (a11a2 >= 30)) || (a11a1 > 13)) {
			a11g1 = a9az;
			a11g2 = a9bz + a11f2;
			a11g3 = a9cz + a11f3;
			a11g4 = a9dz + a11f4;
			a11g5 = a9ez + a11f5;
			if (a11g5 >= 67) {
				a12[4] = a11g5 - 67;
				a11h4 = a11g4 + 1;
			} else {
				a12[4] = a11g5;
				a11h4 = a11g4;
			}
			if (a11h4 >= 6) {
				a12[3] = a11h4 - 6;
				a11h3 = a11g3 + 1;
			} else {
				a12[3] = a11h4;
				a11h3 = a11g3;
			}
			if (a11h3 >= 60) {
				a12[2] = a11h3 - 60;
				a11h2 = a11g2 + 1;
			} else {
				a12[2] = a11h3;
				a11h2 = a11g2;
			}
			if (a11h2 >= 60) {
				a12[1] = a11h2 - 60;
				a11h1 = a11g1 + 1;
			} else {
				a12[1] = a11h2;
				a11h1 = a11g1;
			}
			if (a11h1 >= 27) {
				a12[0] = a11h1 - 27;
			} else {
				a12[0] = a11h1;
			}
			a11i1 = a10e1;
			a11i2 = a10e2 + a11f2;
			a11i3 = a10e3 + a11f3;
			a11i4 = a10e4 + a11f4;
			a11i5 = a10e5ar + a11f5;
			a13[5] = a10e5az;
			if (a11i5 >= 67) {
				a13[4] = a11i5 - 67;
				a11j4 = a11i4 + 1;
			} else {
				a13[4] = a11i5;
				a11j4 = a11i4;
			}
			if (a11j4 >= 6) {
				a13[3] = a11j4 - 6;
				a11j3 = a11i3 + 1;
			} else {
				a13[3] = a11j4;
				a11j3 = a11i3;
			}
			if (a11j3 >= 60) {
				a13[2] = a11j3 - 60;
				a11j2 = a11i2 + 1;
			} else {
				a13[2] = a11j3;
				a11j2 = a11i2;
			}
			if (a11j2 >= 60) {
				a13[1] = a11j2 - 60;
				a11j1 = a11i1 + 1;
			} else {
				a13[1] = a11j2;
				a11j1 = a11i1;
			}
			if (a11j1 >= 7) {
				a13[0] = a11j1 - 7;
			} else {
				a13[0] = a11j1;
			}
		}
		if (((a11a1 == 13) && (a11a2 < 30)) || (a11a1 < 13)) {
			a11g1 = a9az;
			a11g2 = a9bz - a11f2;
			a11g3 = a9cz - a11f3;
			a11g4 = a9dz - a11f4;
			a11g5 = a9ez - a11f5;
			if (a11f2 > a9bz) {
				a12[0] = a11g1 - 1;
				a11h2 = 60 + a11g2;
			} else {
				a12[0] = a11g1;
				a11h2 = a11g2;
			}
			if (a11f3 > a9cz) {
				a12[1] = a11h2 - 1;
				a11h3 = 60 + a11g3;
			} else {
				a12[1] = a11h2;
				a11h3 = a11g3;
			}
			if (a11f4 > a9dz) {
				a12[2] = a11h3 - 1;
				a11h4 = 6 + a11g4;
			} else {
				a12[2] = a11h3;
				a11h4 = a11g4;
			}
			if (a11f5 > a9ez) {
				a12[3] = a11h4 - 1;
				a12[4] = 67 + a11g5;
			} else {
				a12[3] = a11h4;
				a12[4] = a11g5;
			}
			a11i1 = a10e1;
			a11i2 = a10e2 - a11f2;
			a11i3 = a10e3 - a11f3;
			a11i4 = a10e4 - a11f4;
			a11i5 = a10e5ar - a11f5 - 1;
			// a13[6] = 707-a10e5az;
			a13[5] = 707 - a10e5az;
			if (a11f2 > a10e2) {
				a13[0] = a11i1 - 1;
				a11j2 = 60 + a11i2;
			} else {
				a13[0] = a11i1;
				a11j2 = a11i2;
			}
			if (a11f3 > a10e3) {
				a13[1] = a11j2 - 1;
				a11j3 = 60 + a11i3;
			} else {
				a13[1] = a11j2;
				a11j3 = a11i3;
			}
			if (a11f4 > a10e4) {
				a13[2] = a11j3 - 1;
				a11j4 = 6 + a11i4;
			} else {
				a13[2] = a11j3;
				a11j4 = a11i4;
			}
			if ((a11f5 - 1) > a10e5ar) {
				a13[3] = a11j4 - 1;
				a13[4] = 67 + a11i5;
			} else {
				a13[3] = a11j4;
				a13[4] = a11i5;
			}
		}
		int[] a9 = { a9az, a9bz, a9cz, a9dz, a9ez };
		int[][] aRetval = { a12, a13, a9 };
		int[][] aUnits = { { 27, 60, 60, 6, 67 }, { 7, 60, 60, 6, 67, 707 } };

		for (ii = 0; ii <= 1; ii++) {
			for (jj = aRetval[ii].length - 1; jj >= 0; jj--) {
				if (aRetval[ii][jj] < 0) {
					aRetval[ii][jj] += aUnits[ii][jj];
					if (jj != 0) {
						aRetval[ii][jj - 1] -= 1;
					}
				}
			}
		}
		return a13[0];
	}
/*
	public static final void main(String[] args) throws IOException {
		// instantiate the calculator and write out the pre-calculated calendar
		// data for debugging purposes
		TibetanDateCalculation tibCalc = new TibetanDateCalculation();

		String precalculatedData = tibCalc.getPrecalculatedData();

		File f = new File("precalculated.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(precalculatedData, 0, precalculatedData.length());

		writer.close();
	}
	*/
}
