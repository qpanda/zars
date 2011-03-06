package net.soomsam.zirmegghuette.zars.persistence.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = Event.TABLENAME_EVENT)
@NamedQueries({ @NamedQuery(name = Event.FINDEVENT_QUERYNAME, query = Event.FINDEVENT_QUERYSTRING), @NamedQuery(name = Event.FINDEVENTOPENINTERVAL_STARTTIMESTAMP_ENDTIMESTAMP_QUERYNAME, query = Event.FINDEVENTOPENINTERVAL_STARTTIMESTAMP_ENDTIMESTAMP_QUERYSTRING), @NamedQuery(name = Event.FINDEVENT_USERID_QUERYNAME, query = Event.FINDEVENT_USERID_QUERYSTRING) })
public class Event extends BaseEntity {
	public static final String TABLENAME_EVENT = "zars_event";
	public static final String COLUMNNAME_EVENTID = "event_id";
	public static final String COLUMNNAME_EVENTTIMESTAMP = "event_timestamp";
	public static final String COLUMNNAME_CATEGORY = "category";
	public static final String COLUMNNAME_MESSAGE = "message";
	public static final String COLUMNNAME_ENTITYID = "entity_id";
	public static final String COLUMNNAME_ENTITYTYPE = "entity_type";
	public static final String COLUMNNAME_ENTITYOPERATION = "entity_operation";

	public static final int COLUMNLENGTH_CATEGORY = 128;
	public static final int COLUMNLENGTH_MESSAGE = 128;
	public static final int COLUMNLENGTH_ENTITYTYPE = 256;
	public static final int COLUMNLENGTH_ENTITYOPERATION = 128;

	public static final String FINDEVENT_QUERYNAME = "Event.findEventQuery";
	public static final String FINDEVENT_QUERYSTRING = "from Event order by eventTimestamp desc";

	public static final String FINDEVENTOPENINTERVAL_STARTTIMESTAMP_ENDTIMESTAMP_QUERYNAME = "Event.findEventByOpenStartEndIntervalQuery";
	public static final String FINDEVENTOPENINTERVAL_STARTTIMESTAMP_ENDTIMESTAMP_QUERYSTRING = "from Event where :startTimestamp <= eventTimestamp and eventTimestamp <= :endTimestamp order by eventTimestamp desc";

	public static final String FINDEVENT_USERID_QUERYNAME = "Event.findEventByUserIdQuery";
	public static final String FINDEVENT_USERID_QUERYSTRING = "from Event event where event.user.userId = :userId order by eventTimestamp desc";

	@Id
	@GeneratedValue
	@Column(name = Event.COLUMNNAME_EVENTID, unique = true, nullable = false)
	private long eventId;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = Event.COLUMNNAME_EVENTTIMESTAMP)
	private Date eventTimestamp;

	@NotNull
	@NotEmpty
	@Column(name = Event.COLUMNNAME_CATEGORY, nullable = false, length = Event.COLUMNLENGTH_CATEGORY)
	private String category;

	@NotNull
	@NotEmpty
	@Column(name = Event.COLUMNNAME_MESSAGE, nullable = false, length = Event.COLUMNLENGTH_MESSAGE)
	private String message;

	@Column(name = Event.COLUMNNAME_ENTITYID)
	private long entityId;

	@Column(name = Event.COLUMNNAME_ENTITYTYPE, length = Event.COLUMNLENGTH_ENTITYTYPE)
	private String entityType;

	@Column(name = Event.COLUMNNAME_ENTITYOPERATION, length = Event.COLUMNLENGTH_ENTITYOPERATION)
	private String entityOperation;

	@NotNull
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = User.COLUMNNAME_USERID, nullable = false)
	private User user;

	protected Event() {
		super();
	}

	public Event(final String category, final String message, final User user) {
		super();
		this.category = category;
		this.message = message;

		associateUser(user);
	}

	public Event(final String category, final String message, final long entityId, final String entityType, final String entityOperation, final User user) {
		super();
		this.category = category;
		this.message = message;
		this.entityId = entityId;
		this.entityType = entityType;
		this.entityOperation = entityOperation;

		associateUser(user);
	}

	public long getEventId() {
		return eventId;
	}

	void setEventId(final long eventId) {
		this.eventId = eventId;
	}

	public Date getEventTimestamp() {
		return eventTimestamp;
	}

	void setEventTimestamp(final Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getCategory() {
		return category;
	}

	void setCategory(final String category) {
		this.category = category;
	}

	public String getMessage() {
		return message;
	}

	void setMessage(final String message) {
		this.message = message;
	}

	public long getEntityId() {
		return entityId;
	}

	void setEntityId(final long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	void setEntityType(final String entityType) {
		this.entityType = entityType;
	}

	public String getEntityOperation() {
		return entityOperation;
	}

	void setEntityOperation(final String entityOperation) {
		this.entityOperation = entityOperation;
	}

	public User getUser() {
		return user;
	}

	void setUser(final User user) {
		this.user = user;
	}

	public boolean hasUser() {
		return null != getUser();
	}

	protected void associateUser(final User user) {
		if (null == user) {
			throw new IllegalArgumentException("'user' must not be null");
		}

		if (hasUser()) {
			throw new IllegalStateException("event with id [" + eventId + "] is already assigned to user with id [" + getUser().getUserId() + "], re-associate an event to another user is not supported");
		}

		setUser(user);
		user.addEvent(this);
	}

	@Override
	public boolean same(final BaseEntity entity) {
		if (!(entity instanceof Event)) {
			return false;
		}

		final Event other = (Event) entity;
		return new EqualsBuilder().append(getEventId(), other.getEventId()).isEquals();
	}

	@Override
	public boolean sameVersion(final BaseEntity entity) {
		if (!(entity instanceof Event)) {
			return false;
		}

		final Event other = (Event) entity;
		return new EqualsBuilder().append(getEventId(), other.getEventId()).append(getEventTimestamp(), other.getEventTimestamp()).isEquals();
	}

	@Override
	public boolean sameValues(final BaseEntity entity) {
		if (!(entity instanceof Event)) {
			return false;
		}

		final Event other = (Event) entity;
		return new EqualsBuilder().append(getEventId(), other.getEventId()).append(getCategory(), other.getCategory()).append(getMessage(), other.getMessage()).append(getEntityId(), other.getEntityId()).append(getEntityType(), other.getEntityType()).append(getEntityOperation(), other.getEntityOperation()).isEquals();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Event)) {
			return false;
		}

		final Event other = (Event) obj;
		return new EqualsBuilder().append(getEventId(), other.getEventId()).append(getEventTimestamp(), other.getEventTimestamp()).append(getCategory(), other.getCategory()).append(getMessage(), other.getMessage()).append(getEntityId(), other.getEntityId()).append(getEntityType(), other.getEntityType()).append(getEntityOperation(), other.getEntityOperation()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getEventId()).append(getEventTimestamp()).append(getCategory()).append(getMessage()).append(getEntityId()).append(getEntityType()).append(getEntityOperation()).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getEventId()).append(getEventTimestamp()).append(getCategory()).append(getMessage()).append(getEntityId()).append(getEntityType()).append(getEntityOperation()).toString();
	}
}
