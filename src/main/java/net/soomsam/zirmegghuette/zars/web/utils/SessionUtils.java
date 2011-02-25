package net.soomsam.zirmegghuette.zars.web.utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionUtils {
	public static String determineSessionId() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		return determineSessionId(facesContext);
	}

	public static String determineSessionId(final FacesContext facesContext) {
		if (null == facesContext) {
			return null;
		}

		final ExternalContext externalContext = facesContext.getExternalContext();
		if (null == externalContext) {
			return null;
		}

		final Object session = externalContext.getSession(false);
		if (session instanceof HttpSession) {
			final HttpSession httpSession = (HttpSession) session;
			return httpSession.getId();
		}

		return null;
	}

	public static String determineRequestServletPath() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null == facesContext) {
			return null;
		}

		final ExternalContext externalContext = facesContext.getExternalContext();
		if (null == externalContext) {
			return null;
		}

		return externalContext.getRequestServletPath();
	}
}
