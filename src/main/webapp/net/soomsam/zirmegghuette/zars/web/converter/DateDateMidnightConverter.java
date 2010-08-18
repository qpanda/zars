package net.soomsam.zirmegghuette.zars.web.converter;

import java.util.Date;

import org.dozer.DozerConverter;
import org.joda.time.DateMidnight;

public class DateDateMidnightConverter extends DozerConverter<Date, DateMidnight> {
	public DateDateMidnightConverter() {
		super(Date.class, DateMidnight.class);
	}

	@Override
	public Date convertFrom(DateMidnight source, Date destination) {
		if (null == source) {
			return null;
		}

		return source.toDate();
	}

	@Override
	public DateMidnight convertTo(Date source, DateMidnight destination) {
		if (null == source) {
			return null;
		}

		return new DateMidnight(source);
	}
}