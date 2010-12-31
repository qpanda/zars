package net.soomsam.zirmegghuette.zars.web.converter;

import java.util.Date;

import org.dozer.DozerConverter;
import org.joda.time.DateMidnight;

public class DateDateMidnightConverter extends DozerConverter<Date, DateMidnight> {
	public DateDateMidnightConverter() {
		super(Date.class, DateMidnight.class);
	}

	@Override
	public Date convertFrom(final DateMidnight source, final Date destination) {
		if (null == source) {
			return null;
		}

		return source.toDate();
	}

	@Override
	public DateMidnight convertTo(final Date source, final DateMidnight destination) {
		if (null == source) {
			return null;
		}

		return new DateMidnight(source);
	}
}