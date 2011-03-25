package net.soomsam.zirmegghuette.zars.service;

import java.util.Locale;
import java.util.TimeZone;

import net.soomsam.zirmegghuette.zars.enums.PreferenceType;
import net.soomsam.zirmegghuette.zars.service.bean.PreferenceBean;

import org.springframework.security.access.prepost.PreAuthorize;

public interface PreferenceService {
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PreferenceBean createPreference(long userId, PreferenceType preferenceType, Object value);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PreferenceBean findPreference(long userId, PreferenceType preferenceType);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public PreferenceBean updatePreference(long userId, PreferenceType preferenceType, Object value);

	@PreAuthorize("isAuthenticated()")
	public PreferenceBean findCurrentUserPreference(PreferenceType preferenceType);

	@PreAuthorize("isAuthenticated()")
	public PreferenceBean updateCurrentUserPreference(PreferenceType preferenceType, Object value);

	@PreAuthorize("isAuthenticated()")
	public Locale determinePreferredLocale(long userId);

	@PreAuthorize("isAuthenticated()")
	public TimeZone determinePreferredTimeZone(long userId);

	@PreAuthorize("isAuthenticated()")
	public boolean determineNotification(final long userId);
}
