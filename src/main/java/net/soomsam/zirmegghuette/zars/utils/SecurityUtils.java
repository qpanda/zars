package net.soomsam.zirmegghuette.zars.utils;

import java.util.Collection;

import net.soomsam.zirmegghuette.zars.enums.RoleType;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public final static boolean hasRole(final RoleType role) {
		return hasRole(role.getRoleName());
	}

	public final static boolean hasRole(final String role) {
		final Authentication authentication = determineAuthentication();
		final Collection<GrantedAuthority> grantedAuthorityCollection = authentication.getAuthorities();
		return isAuthorityPresent(grantedAuthorityCollection, role);
	}

	public final static Authentication determineAuthentication() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			throw new NoAuthenticationException("no authentication information stored/available");
		}

		return authentication;
	}

	public final static String determineUsername() {
		return determineAuthentication().getName();
	}

	public final static boolean isFullyAuthenticated() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			return false;
		}

		final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
		return !authenticationTrustResolver.isAnonymous(authentication);
	}

	public final static boolean isAuthorityPresent(final Collection<GrantedAuthority> grantedAuthorityCollection, final String role) {
		for (final GrantedAuthority grantedAuthority : grantedAuthorityCollection) {
			final String authority = grantedAuthority.getAuthority();
			if (null == authority) {
				throw new UnsupportedAuthorityException("no 'String' representation of authority available");
			}

			if (authority.equals(role)) {
				return true;
			}
		}

		return false;
	}
}
