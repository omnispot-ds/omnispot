/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * Class including date-related operations.
 * 
 * @author gerogias
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(DateUtils.class);

	/**
	 * Format for date-to-text formatting.
	 */
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	/**
	 * Format for date-time-to-text formatting.
	 */
	public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Calculates the difference in time units between the two dates.
	 * 
	 * @param d1
	 *            the first date
	 * @param d2
	 *            the second date
	 * @param field
	 *            the time field to compare; it can be one of
	 *            <ul>
	 *            <li>{@link Calendar#SECOND}</li>
	 *            <li>{@link Calendar#MINUTE}</li>
	 *            <li>{@link Calendar#HOUR}</li>
	 *            <li>{@link Calendar#DAY_OF_YEAR}</li>
	 *            </ul>
	 * @return long the difference, which shows how many time units d1 is before
	 *         d2, without the application of any rounding. If the difference is
	 *         negative, it means that d2 is before d1.
	 * @throws IllegalArgumentException
	 *             if either argument is <code>null</code> or the field is not
	 *             supported
	 */
	public static long difference(Date d1, Date d2, int field) {

		if (d1 == null || d2 == null) {
			logger.error("Dates cannot be null");
			throw new IllegalArgumentException("Dates cannot be null");
		}

		if (field != Calendar.SECOND && field != Calendar.MINUTE
				&& field != Calendar.HOUR && field != Calendar.DAY_OF_YEAR) {
			logger.error("Unsupported field type");
			throw new IllegalArgumentException("Unsupported field type");
		}

		switch (field) {

		case (Calendar.SECOND): {
			return (d2.getTime() - d1.getTime()) / 1000;
		}

		case (Calendar.MINUTE): {
			return (d2.getTime() - d1.getTime()) / MILLIS_PER_MINUTE;
		}

		case (Calendar.HOUR): {
			return (d2.getTime() - d1.getTime()) / MILLIS_PER_HOUR;
		}

		case (Calendar.DAY_OF_YEAR): {
			return (d2.getTime() - d1.getTime()) / MILLIS_PER_DAY;
		}
			// unreachable
		default: {
			return 0L;
		}
		}
	}

	/**
	 * Given a date, returns a new object "pointing" to the start of that day.
	 * 
	 * @param date
	 *            the date
	 * @return Date the new object
	 * @throws IllegalArgumentException
	 *             if the argument was <code>null</code>
	 */
	public static Date getStartOfDay(Date date) {
		if (date == null) {
			logger.error("The date is null");
			throw new IllegalArgumentException("The date is null");
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * Given a date, returns a new object "pointing" to the end of that day.
	 * 
	 * @param date
	 *            the date
	 * @return Date the new object
	 * @throws IllegalArgumentException
	 *             if the argument was <code>null</code>
	 */
	public static Date getEndOfDay(Date date) {
		if (date == null) {
			logger.error("The date is null");
			throw new IllegalArgumentException("The date is null");
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

}
