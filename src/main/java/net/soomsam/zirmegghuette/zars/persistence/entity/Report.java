package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.soomsam.zirmegghuette.zars.persistence.utils.DateUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateMidnight;

@Entity
@Table(name = Report.TABLENAME_REPORT)
public class Report extends BaseEntity {
	public static final String TABLENAME_REPORT = "zars_report";
	public static final String COLUMNNAME_REPORTID = "report_id";
	public static final String COLUMNNAME_REPORTTIMESTAMP = "report_timestamp";
	public static final String COLUMNNAME_DATE = "date";
	public static final String COLUMNNAME_PERIODSTART = "period_start";
	public static final String COLUMNNAME_PERIODEND = "period_end";
	public static final String COLUMNNAME_DOCUMENT = "document";
	public static final String COLUMNNAME_STALE = "stale";
	public static final String JOINTABLENAME_REPORT_GROUPRESERVATION = "zars_report_zars_group_reservation";

	@Id
	@GeneratedValue
	@Column(name = Report.COLUMNNAME_REPORTID, unique = true, nullable = false)
	private long reportId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Report.COLUMNNAME_REPORTTIMESTAMP, nullable = false)
	private Date reportTimestamp;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Report.COLUMNNAME_DATE, nullable = false)
	private Date date;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = Report.COLUMNNAME_PERIODSTART, nullable = false)
	private Date periodStart;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = Report.COLUMNNAME_PERIODEND, nullable = false)
	private Date periodEnd;

	@NotNull
	@Lob
	@Column(name = Report.COLUMNNAME_DOCUMENT, nullable = false)
	@Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
	private byte[] document;

	@NotNull
	@Column(name = Report.COLUMNNAME_STALE, nullable = false)
	private boolean stale;

	@NotNull
	@Size(min = 1)
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = Report.JOINTABLENAME_REPORT_GROUPRESERVATION, joinColumns = @JoinColumn(name = Report.COLUMNNAME_REPORTID), inverseJoinColumns = @JoinColumn(name = GroupReservation.COLUMNNAME_GROUPRESERVATIONID))
	private final Set<GroupReservation> groupReservations = new HashSet<GroupReservation>(0);

	protected Report() {
		super();
		this.stale = false;
	}

	public Report(final Date date, final DateMidnight periodStart, final DateMidnight periodEnd, final byte[] document, final GroupReservation groupReservation) {
		super();
		this.stale = false;
		this.date = date;
		this.periodStart = DateUtils.convertDateMidnight(periodStart);
		this.periodEnd = DateUtils.convertDateMidnight(periodEnd);
		this.document = document;

		associateGroupReservation(groupReservation);
	}

	public Report(final Date date, final DateMidnight periodStart, final DateMidnight periodEnd, final byte[] document, final Set<GroupReservation> groupReservations) {
		super();
		this.stale = false;
		this.date = date;
		this.periodStart = DateUtils.convertDateMidnight(periodStart);
		this.periodEnd = DateUtils.convertDateMidnight(periodEnd);
		this.document = document;

		associateGroupReservations(groupReservations);
	}

	public long getReportId() {
		return reportId;
	}

	void setReportId(final long reportId) {
		this.reportId = reportId;
	}

	public Date getReportTimestamp() {
		return reportTimestamp;
	}

	void setReportTimestamp(final Date reportTimestamp) {
		this.reportTimestamp = reportTimestamp;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public DateMidnight getPeriodStart() {
		return DateUtils.convertDate(periodStart);
	}

	public void setPeriodStart(final DateMidnight periodStart) {
		this.periodStart = DateUtils.convertDateMidnight(periodStart);
	}

	public DateMidnight getPeriodEnd() {
		return DateUtils.convertDate(periodEnd);
	}

	public void setPeriodEnd(final DateMidnight periodEnd) {
		this.periodEnd = DateUtils.convertDateMidnight(periodEnd);
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(final byte[] document) {
		this.document = document;
	}

	public boolean isStale() {
		return stale;
	}

	public void setStale(final boolean stale) {
		this.stale = stale;
	}

	public Set<GroupReservation> getGroupReservations() {
		return Collections.unmodifiableSet(groupReservations);
	}

	void addGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		this.groupReservations.add(groupReservation);
	}

	void addGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		this.groupReservations.addAll(groupReservationSet);
	}

	void removeGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		this.groupReservations.remove(groupReservation);
	}

	protected void associateGroupReservation(final GroupReservation groupReservation) {
		if (null == groupReservation) {
			throw new IllegalArgumentException("'groupReservation' must not be null");
		}

		addGroupReservation(groupReservation);
		groupReservation.addReport(this);
	}

	protected void associateGroupReservations(final Set<GroupReservation> groupReservationSet) {
		if (null == groupReservationSet) {
			throw new IllegalArgumentException("'groupReservationSet' must not be null");
		}

		for (final GroupReservation groupReservation : groupReservationSet) {
			groupReservation.addReport(this);
		}

		addGroupReservations(groupReservationSet);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Report)) {
			return false;
		}

		final Report other = (Report) entity;
		return new EqualsBuilder().append(getReportId(), other.getReportId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Report)) {
			return false;
		}

		final Report other = (Report) entity;
		return new EqualsBuilder().append(getReportId(), other.getReportId()).append(getReportTimestamp(), other.getReportTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Report)) {
			return false;
		}

		final Report other = (Report) entity;
		return new EqualsBuilder().append(getReportId(), other.getReportId()).append(getDate(), other.getDate()).append(getPeriodStart(), other.getPeriodStart()).append(getPeriodEnd(), other.getPeriodEnd()).append(isStale(), other.isStale()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Report)) {
			return false;
		}

		final Report other = (Report) obj;
		return new EqualsBuilder().append(getReportId(), other.getReportId()).append(getReportTimestamp(), other.getReportTimestamp()).append(getDate(), other.getDate()).append(getPeriodStart(), other.getPeriodStart()).append(getPeriodEnd(), other.getPeriodEnd()).append(isStale(), other.isStale()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getReportId()).append(getReportTimestamp()).append(getDate()).append(getPeriodStart()).append(getPeriodEnd()).append(isStale()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getReportId()).append(getReportTimestamp()).append(getDate()).append(getPeriodStart()).append(getPeriodEnd()).append(isStale()).toString();
	}
}
