package net.soomsam.zirmegghuette.zars.web.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class JsfContextUtils {
	public static String determineSessionId() {
		Object session = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session instanceof HttpSession) {
			HttpSession httpSession = (HttpSession) session;
			return httpSession.getId();
		}

		return null;
	}
}
