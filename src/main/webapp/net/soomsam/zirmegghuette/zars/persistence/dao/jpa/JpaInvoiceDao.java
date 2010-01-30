package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import net.soomsam.zirmegghuette.zars.persistence.dao.InvoiceDao;
import net.soomsam.zirmegghuette.zars.persistence.entity.Invoice;

import org.springframework.stereotype.Repository;

@Repository("invoiceDao")
public class JpaInvoiceDao extends JpaBaseDao<Invoice> implements InvoiceDao {
	@Override
	protected Class<Invoice> determineEntityClass() {
		return Invoice.class;
	}
}
