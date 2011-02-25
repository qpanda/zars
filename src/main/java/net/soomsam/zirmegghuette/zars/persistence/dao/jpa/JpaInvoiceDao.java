package net.soomsam.zirmegghuette.zars.persistence.dao.jpa;

import java.util.Set;

import net.soomsam.zirmegghuette.zars.persistence.dao.InvoiceDao;
import net.soomsam.zirmegghuette.zars.persistence.dao.OperationNotSupportedException;
import net.soomsam.zirmegghuette.zars.persistence.entity.Invoice;

import org.springframework.stereotype.Repository;

@Repository("invoiceDao")
public class JpaInvoiceDao extends JpaEntityDao<Invoice> implements InvoiceDao {
	@Override
	protected Class<Invoice> determineEntityClass() {
		return Invoice.class;
	}

	@Override
	public void remove(final Invoice entity) {
		throw new OperationNotSupportedException("[" + JpaInvoiceDao.class.getSimpleName() + "] does not support operation 'remove'");
	}

	@Override
	public void removeAll(final Set<Invoice> entitySet) {
		throw new OperationNotSupportedException("[" + JpaInvoiceDao.class.getSimpleName() + "] does not support operation 'removeAll'");
	}
}
