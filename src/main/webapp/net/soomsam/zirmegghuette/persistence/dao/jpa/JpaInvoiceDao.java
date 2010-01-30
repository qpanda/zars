package net.soomsam.zirmegghuette.persistence.dao.jpa;

import net.soomsam.zirmegghuette.persistence.dao.InvoiceDao;
import net.soomsam.zirmegghuette.persistence.entity.Invoice;

import org.springframework.stereotype.Repository;

@Repository("invoiceDao")
public class JpaInvoiceDao extends JpaBaseDao<Invoice> implements InvoiceDao {
	@Override
	protected Class<Invoice> determineEntityClass() {
		return Invoice.class;
	}
}
