package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.event.JPAValidateListener;

@Entity
@Table(name = Invoice.TABLENAME_INVOICE)
@EntityListeners(value = { JPAValidateListener.class })
public class Invoice extends BaseEntity {
	public static final String TABLENAME_INVOICE = "invoice";
	public static final String COLUMNNAME_INVOICEID = "invoice_id";
	public static final String COLUMNNAME_DATE = "date";
	public static final String COLUMNNAME_AMOUNT = "amount";
	public static final String COLUMNNAME_PAYED = "payed";

	@Id
	@GeneratedValue
	@Column(name = Invoice.COLUMNNAME_INVOICEID, unique = true, nullable = false)
	private long invoiceId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Invoice.COLUMNNAME_DATE, nullable = false)
	private Date date;

	@Column(name = Invoice.COLUMNNAME_AMOUNT, nullable = false)
	private float amount;

	@Column(name = Invoice.COLUMNNAME_PAYED, nullable = false)
	private boolean payed;

	private Invoice() {
		super();
	}

	public Invoice(final Date date, final float amount, final boolean payed) {
		super();
		this.date = date;
		this.amount = amount;
		this.payed = payed;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(final long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(final float amount) {
		this.amount = amount;
	}

	public boolean isPayed() {
		return payed;
	}

	public void setPayed(final boolean payed) {
		this.payed = payed;
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Invoice)) {
			return false;
		}

		final Invoice other = (Invoice) entity;
		return new EqualsBuilder().append(getInvoiceId(), other.getInvoiceId()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Invoice)) {
			return false;
		}

		final Invoice other = (Invoice) obj;
		return new EqualsBuilder().append(getInvoiceId(), other.getInvoiceId()).append(getDate(), other.getDate()).append(getAmount(), other.getAmount()).append(isPayed(), other.isPayed()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getInvoiceId()).append(getDate()).append(getAmount()).append(isPayed()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getInvoiceId()).append(getDate()).append(getAmount()).append(isPayed()).toString();
	}
}
