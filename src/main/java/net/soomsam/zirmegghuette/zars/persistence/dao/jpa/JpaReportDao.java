package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.ReportDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Report;

import org.springframework.stereotype.Repository;

@Repository("reportDao")
public class JpaReportDao extends JpaEntityDao<Report> implements ReportDao {
	@Override
	protected Class<Report> determineEntityClass() {
		return Report.class;
	}
}
