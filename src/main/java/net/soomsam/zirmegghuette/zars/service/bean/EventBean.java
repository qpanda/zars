package net.soomsam.zirmegghuette.zars.service.bean;

import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class EventBean extends BaseBean {
	private long eventId;

	private Date eventTimestamp;

	private String category;

	private String message;

	private long entityId;

	private String entityType;

	private String entityOperation;

	private UserBean user;

	public EventBean() {
		super();
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(final long eventId) {
		this.eventId = eventId;
	}

	public Date getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(final Date eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(final long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(final String entityType) {
		this.entityType = entityType;
	}

	public String getEntityOperation() {
		return entityOperation;
	}

	public void setEntityOperation(final String entityOperation) {
		this.entityOperation = entityOperation;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(final UserBean user) {
		this.user = user;
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[] { "eventTimestamp" });
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[] { "eventTimestamp" });
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
