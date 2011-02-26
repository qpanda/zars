package net.soomsam.zirmegghuette.zars.service.transactional;

import java.util.List;

import net.soomsam.zirmegghuette.zars.persistence.dao.EventDao;
import net.soomsam.zirmegghuette.zars.service.EventService;
import net.soomsam.zirmegghuette.zars.service.bean.EventBean;
import net.soomsam.zirmegghuette.zars.service.utils.ServiceBeanMapper;
import net.soomsam.zirmegghuette.zars.utils.Pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("eventService")
@Transactional(timeout = 10000)
public class TransactionalEventService implements EventService {
	@Autowired
	private ServiceBeanMapper serviceBeanMapper;

	@Autowired
	private EventDao eventDao;

	@Override
	@Transactional(readOnly = true)
	public List<EventBean> findLatestEvents(final Pagination pagination) {
		return serviceBeanMapper.map(EventBean.class, eventDao.findLatest(pagination));
	}
}
