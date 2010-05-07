package net.soomsam.zirmegghuette.zars.persistence.utils;

import java.util.Date;

import org.joda.time.DateMidnight;

public class DateUtils {
	public static Date convertDateMidnight(final DateMidnight dateMidnight) {
		if (null == dateMidnight) {
			return null;
		}

		return dateMidnight.toDate();
	}

	public static DateMidnight convertDate(final Date date) {
		if (null == date) {
			return null;
		}

		return new DateMidnight(date);
	}
}
