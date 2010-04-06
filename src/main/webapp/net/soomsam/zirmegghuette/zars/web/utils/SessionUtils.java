package net.soomsam.zirmegghuette.zars.web.utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionUtils {
	public static String determineSessionId() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return null;
		}

		ExternalContext externalContext = facesContext.getExternalContext();
		if (null == externalContext) {
			return null;
		}

		Object session = externalContext.getSession(false);
		if (session instanceof HttpSession) {
			HttpSession httpSession = (HttpSession) session;
			return httpSession.getId();
		}

		return null;
	}
}
