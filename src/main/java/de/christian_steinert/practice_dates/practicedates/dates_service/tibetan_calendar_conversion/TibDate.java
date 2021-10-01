package de.christian_steinert.practice_dates.practicedates.dates_service.tibetan_calendar_conversion;

/**
 * A simple data class that represents a Tibetan date
 */
public class TibDate {
	/**
	 * Constructor
	 * 
	 * @param rabjung
	 *            number of the Tibetan 60-year-cycle
	 * @param tibYear
	 *            number of the year within the rabjung (1..60)
	 * @param tibMonth
	 *            number of the Tibetan month (1..12)
	 * @param monthFlag
	 *            0: normal month, 1: first month of a double month, 2: second
	 *            month of a double month
	 * @param tibDay
	 *            number of the day within the Tibetan month (1..30)
	 */
	public TibDate(int rabjung, int tibYear, int tibMonth, int monthFlag,
			int tibDay) {
		super();
		this.rabjung = rabjung;
		this.tibYear = tibYear;
		this.tibMonth = tibMonth;
		this.monthFlag = monthFlag;
		this.tibDay = tibDay;

		this.isSkippedDay = false;
		this.doubleDayFlag = 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.monthFlag;
		result = prime * result + this.rabjung;
		result = prime * result + this.tibDay;
		result = prime * result + this.tibMonth;
		result = prime * result + this.tibYear;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		TibDate other = (TibDate) obj;
		if (this.monthFlag != other.monthFlag)
			return false;
		if (this.rabjung != other.rabjung)
			return false;
		if (this.tibDay != other.tibDay)
			return false;
		if (this.tibMonth != other.tibMonth)
			return false;
		if (this.tibYear != other.tibYear)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TibDate [rabjung=" + this.rabjung + ", tibYear=" + this.tibYear
				+ ", tibMonth=" + this.tibMonth + ", monthFlag="
				+ this.monthFlag + ", tibDay=" + this.tibDay
				+ ", isSkippedDay=" + this.isSkippedDay + ", doubleDayFlag="
				+ this.doubleDayFlag + "]";
	}

	/** number of the rabjung (60-year-cycle) */
	public int rabjung;

	/** number of the Tibetan year within the rabjung */
	public int tibYear;

	/** number of the Tibetan month */
	public int tibMonth;

	/**
	 * type of month - 0: this month is a normal month; 1: this month is the
	 * first month of a double month; 2: this month is the second month of a
	 * double month
	 */
	public int monthFlag;

	/** number of the Tibetan day in the Tibetan month */
	public int tibDay;

	/**
	 * if "true" then this day was skipped and therefore it has no corresponding
	 * western Date
	 */
	public boolean isSkippedDay;

	/**
	 * type of day - 0: normal day; 1: first day of a double day; 2: second day
	 * of a double day
	 */
	public int doubleDayFlag;

}