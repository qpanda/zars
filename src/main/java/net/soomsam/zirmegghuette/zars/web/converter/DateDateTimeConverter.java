package net.soomsam.zirmegghuette.zars.web.converter;

import java.util.Date;

import org.dozer.DozerConverter;
import org.joda.time.DateTime;

public class DateDateTimeConverter extends DozerConverter<Date, DateTime> {
	public DateDateTimeConverter() {
		super(Date.class, DateTime.class);
	}

	@Override
	public Date convertFrom(final DateTime source, final Date destination) {
		if (null == source) {
			return null;
		}

		return source.toDate();
	}

	@Override
	public DateTime convertTo(final Date source, final DateTime destination) {
		if (null == source) {
			return null;
		}

		return new DateTime(source);
	}
}