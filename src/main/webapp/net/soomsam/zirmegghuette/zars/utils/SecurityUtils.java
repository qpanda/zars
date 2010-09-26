package net.soomsam.zirmegghuette.zars.utils;

import java.util.Collection;

import net.soomsam.zirmegghuette.zars.enums.RoleType;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
	public final static boolean hasRole(RoleType role) {
		return hasRole(role.getRoleName());
	}

	public final static boolean hasRole(String role) {
		Authentication authentication = determineAuthentication();
		Collection<GrantedAuthority> grantedAuthorityCollection = authentication.getAuthorities();
		return isAuthorityPresent(grantedAuthorityCollection, role);
	}

	public final static Authentication determineAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			throw new NoAuthenticationException("no authentication information stored/available");
		}

		return authentication;
	}

	public final static boolean isAuthorityPresent(Collection<GrantedAuthority> grantedAuthorityCollection, String role) {
		for (GrantedAuthority grantedAuthority : grantedAuthorityCollection) {
			String authority = grantedAuthority.getAuthority();
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
