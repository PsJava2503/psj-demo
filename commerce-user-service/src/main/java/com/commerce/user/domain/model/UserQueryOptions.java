package com.commerce.user.domain.model;

import java.time.ZonedDateTime;
import java.util.Optional;

public class UserQueryOptions {

	private final Optional<Long> id;
	private final Optional<String> firstName;
	private final Optional<String> secondName;
	private final Optional<String> phone;
	private final Optional<String> email;
	private final Optional<Long> defaultAddressSlotId;
	private final Optional<Boolean> enabled;
	private final Optional<Boolean> deleted;
	private final Optional<ZonedDateTime> createTime;
	private final Optional<ZonedDateTime> updateTime;

	public UserQueryOptions(
			Optional<Long> id,
			Optional<String> firstName,
			Optional<String> secondName,
			Optional<String> phone,
			Optional<String> email,
			Optional<Long> defaultAddressSlotId,
			Optional<Boolean> enabled,
			Optional<Boolean> deleted,
			Optional<ZonedDateTime> createTime,
			Optional<ZonedDateTime> updateTime
	) {
		this.id = id == null ? Optional.empty() : id;
		this.firstName = firstName == null ? Optional.empty() : firstName;
		this.secondName = secondName == null ? Optional.empty() : secondName;
		this.phone = phone == null ? Optional.empty() : phone;
		this.email = email == null ? Optional.empty() : email;
		this.defaultAddressSlotId = defaultAddressSlotId == null ? Optional.empty() : defaultAddressSlotId;
		this.enabled = enabled == null ? Optional.empty() : enabled;
		this.deleted = deleted == null ? Optional.empty() : deleted;
		this.createTime = createTime == null ? Optional.empty() : createTime;
		this.updateTime = updateTime == null ? Optional.empty() : updateTime;
	}

	public static UserQueryOptions none() {
		return new UserQueryOptions(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
		);
	}

	public Optional<Long> getId() {
		return id;
	}

	public Optional<String> getFirstName() {
		return firstName;
	}

	public Optional<String> getSecondName() {
		return secondName;
	}

	public Optional<String> getPhone() {
		return phone;
	}

	public Optional<String> getEmail() {
		return email;
	}

	public Optional<Long> getDefaultAddressSlotId() {
		return defaultAddressSlotId;
	}

	public Optional<Boolean> getEnabled() {
		return enabled;
	}

	public Optional<Boolean> getDeleted() {
		return deleted;
	}

	public Optional<ZonedDateTime> getCreateTime() {
		return createTime;
	}

	public Optional<ZonedDateTime> getUpdateTime() {
		return updateTime;
	}

	public boolean hasId() {
		return id.isPresent();
	}

	public boolean hasFirstName() {
		return firstName.isPresent();
	}

	public boolean hasSecondName() {
		return secondName.isPresent();
	}

	public boolean hasPhone() {
		return phone.isPresent();
	}

	public boolean hasEmail() {
		return email.isPresent();
	}

	public boolean hasDefaultAddressSlotId() {
		return defaultAddressSlotId.isPresent();
	}

	public boolean hasEnabled() {
		return enabled.isPresent();
	}

	public boolean hasDeleted() {
		return deleted.isPresent();
	}

	public boolean hasCreateTime() {
		return createTime.isPresent();
	}

	public boolean hasUpdateTime() {
		return updateTime.isPresent();
	}

	public Long getIdValue() {
		return id.orElse(null);
	}

	public String getFirstNameValue() {
		return firstName.orElse(null);
	}

	public String getSecondNameValue() {
		return secondName.orElse(null);
	}

	public String getPhoneValue() {
		return phone.orElse(null);
	}

	public String getEmailValue() {
		return email.orElse(null);
	}

	public Long getDefaultAddressSlotIdValue() {
		return defaultAddressSlotId.orElse(null);
	}

	public Boolean getEnabledValue() {
		return enabled.orElse(null);
	}

	public Boolean getDeletedValue() {
		return deleted.orElse(null);
	}

	public ZonedDateTime getCreateTimeValue() {
		return createTime.orElse(null);
	}

	public ZonedDateTime getUpdateTimeValue() {
		return updateTime.orElse(null);
	}
}
