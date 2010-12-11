package net.soomsam.zirmegghuette.zars.web.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import net.soomsam.zirmegghuette.zars.utils.NoAuthenticationException;
import net.soomsam.zirmegghuette.zars.utils.SecurityUtils;
import net.soomsam.zirmegghuette.zars.web.utils.SessionUtils;

import org.apache.log4j.MDC;

public class LogMdcPhaseListener implements PhaseListener {
	public static final String MDC_SESSION_ID = "SESSION_ID";
	public static final String MDC_USERNAME = "USERNAME";

	@Override
	public void beforePhase(final PhaseEvent phaseEvent) {
		addSessionId(phaseEvent);
		addUsername();
	}

	protected void addSessionId(final PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		String sessionId = SessionUtils.determineSessionId(facesContext);
		if (null != sessionId) {
			MDC.put(MDC_SESSION_ID, sessionId);
		}
	}

	protected void addUsername() {
		try {
			String user = SecurityUtils.determineUsername();
			MDC.put(MDC_USERNAME, user);
		} catch (NoAuthenticationException noAuthenticationException) {
		}
	}

	@Override
	public void afterPhase(final PhaseEvent phaseEvent) {
		removeSessionId();
		removeUsername();
	}

	protected void removeSessionId() {
		MDC.remove(MDC_SESSION_ID);
	}

	protected void removeUsername() {
		MDC.remove(MDC_USERNAME);
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
