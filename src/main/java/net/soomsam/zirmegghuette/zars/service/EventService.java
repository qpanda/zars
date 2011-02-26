package net.soomsam.zirmegghuette.zars.service;

import java.util.List;
import net.soomsam.zirmegghuette.zars.service.bean.EventBean;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.springframework.security.access.prepost.PreAuthorize;

public interface EventService {
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<EventBean> findLatestEvents(final Pagination pagination);
}
