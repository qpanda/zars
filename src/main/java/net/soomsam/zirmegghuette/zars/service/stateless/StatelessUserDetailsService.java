package net.soomsam.zirmegghuette.zars.service.stateless;

import java.util.ArrayList;
import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.dao.EntityNotFoundException;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class StatelessUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
		try {
			final UserBean userBean = userService.retrieveUser(username);

			final List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
			for (final RoleBean roleBean : userBean.getRoles()) {
				grantedAuthorityList.add(new GrantedAuthorityImpl(roleBean.getName()));
			}

			return new User(userBean.getUsername(), userBean.getPassword(), userBean.isEnabled(), true, true, true, grantedAuthorityList);
		} catch (final EntityNotFoundException entityNotFoundException) {
			throw new UsernameNotFoundException("unable to load user with username [" + username + "]", entityNotFoundException);
		}
	}
}
