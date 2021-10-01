package de.christian_steinert.practice_dates.practicedates.dates_service.tibetan_calendar_conversion;

import java.util.Date;

/**
 * A simple data class that represents the pair of a Tibetan date together with
 * its corresponding Western date
 */
public class DatePair {
	/** the Tibetan date */
	public final TibDate tibDate;
	/** the corresponding Western date */
	public final Date westernDate;

	/**
	 * Constructor
	 * 
	 * @param tibDate
	 *            a Tibetan date
	 * @param westernDate
	 *            the corresponding Western date
	 */
	public DatePair(TibDate tibDate, Date westernDate) {
		super();
		this.tibDate = tibDate;
		this.westernDate = westernDate;
	}
}