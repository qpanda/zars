package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = Invoice.TABLENAME_INVOICE)
public class Invoice extends BaseEntity {
	public static final String TABLENAME_INVOICE = "zars_invoice";
	public static final String COLUMNNAME_INVOICEID = "invoice_id";
	public static final String COLUMNNAME_INVOICETIMESTAMP = "invoice_timestamp";
	public static final String COLUMNNAME_DATE = "date";
	public static final String COLUMNNAME_CURRENCY = "currency";
	public static final String COLUMNNAME_AMOUNT = "amount";
	public static final String COLUMNNAME_STALE = "stale";
	public static final String COLUMNNAME_PAYED = "payed";
	public static final String COLUMNNAME_DOCUMENT = "document";

	public static final int COLUMNLENGTH_CURRENCY = 3;

	@Id
	@GeneratedValue
	@Column(name = Invoice.COLUMNNAME_INVOICEID, unique = true, nullable = false)
	private long invoiceId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Invoice.COLUMNNAME_INVOICETIMESTAMP, nullable = false)
	private Date invoiceTimestamp;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Invoice.COLUMNNAME_DATE, nullable = false)
	private Date date;

	@NotNull
	@NotEmpty
	@Column(name = Invoice.COLUMNNAME_CURRENCY, nullable = false, length = Invoice.COLUMNLENGTH_CURRENCY)
	private String currency;

	@Min(value = 0)
	@Column(name = Invoice.COLUMNNAME_AMOUNT, nullable = false)
	private double amount;

	@NotNull
	@Column(name = Invoice.COLUMNNAME_STALE, nullable = false)
	private boolean stale;

	@NotNull
	@Column(name = Invoice.COLUMNNAME_PAYED, nullable = false)
	private boolean payed;

	@NotNull
	@Lob
	@Column(name = Invoice.COLUMNNAME_DOCUMENT, nullable = false)
	@Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
	private byte[] document;

	@NotNull
	@OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID, unique = true, nullable = true, updatable = false)
	private GroupReservation groupReservation;

	protected Invoice() {
		super();
	}

	public Invoice(final Date date, final String currency, final double amount, final boolean payed, final byte[] document, final GroupReservation groupReservation) {
		super();
		this.date = date;
		this.currency = currency;
		this.amount = amount;
		this.payed = payed;
		this.document = document;

		associateGroupReservation(groupReservation);
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	void setInvoiceId(final long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Date getInvoiceTimestamp() {
		return invoiceTimestamp;
	}

	void setInvoiceTimestamp(final Date invoiceTimestamp) {
		this.invoiceTimestamp = invoiceTimestamp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
	}

	public boolean isPayed() {
		return payed;
	}

	public void setPayed(final boolean payed) {
		this.payed = payed;
	}

	public boolean isStale() {
		return stale;
	}

	public void setStale(final boolean stale) {
		this.stale = stale;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(final byte[] document) {
		this.document = document;
	}

	public GroupReservation getGroupReservation() {
		return groupReservation;
	}

	void setGroupReservation(final GroupReservation groupReservation) {
		this.groupReservation = groupReservation;
	}

	protected void associateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		setGroupReservation(groupReservation);
		groupReservation.setInvoice(this);
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
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Invoice)) {
			return false;
		}

		final Invoice other = (Invoice) entity;
		return new EqualsBuilder().append(getInvoiceId(), other.getInvoiceId()).append(getInvoiceTimestamp(), other.getInvoiceTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Invoice)) {
			return false;
		}

		final Invoice other = (Invoice) entity;
		return new EqualsBuilder().append(getInvoiceId(), other.getInvoiceId()).append(getDate(), other.getDate()).append(getCurrency(), other.getCurrency()).append(getAmount(), other.getAmount()).append(isPayed(), other.isPayed()).append(isStale(), other.isStale()).isEquals();
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
		return new EqualsBuilder().append(getInvoiceId(), other.getInvoiceId()).append(getInvoiceTimestamp(), other.getInvoiceTimestamp()).append(getDate(), other.getDate()).append(getCurrency(), other.getCurrency()).append(getAmount(), other.getAmount()).append(isPayed(), other.isPayed()).append(isStale(), other.isStale()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getInvoiceId()).append(getInvoiceTimestamp()).append(getDate()).append(getCurrency()).append(getAmount()).append(isPayed()).append(isStale()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getInvoiceId()).append(getInvoiceTimestamp()).append(getDate()).append(getCurrency()).append(getAmount()).append(isPayed()).append(isStale()).toString();
	}
}
