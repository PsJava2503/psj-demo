package com.commerce.notification.infrastructure.persistence;

import com.commerce.notification.domain.model.NotificationRecordQueryOptions;
import com.commerce.notification.infrastructure.persistence.mapper.NotificationRecordDynamicMapper;
import com.commerce.notification.infrastructure.persistence.model.NotificationRecordData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRecordRepository {

	private final NotificationRecordDynamicMapper mapper;

	public NotificationRecordRepository(NotificationRecordDynamicMapper mapper) {
		this.mapper = mapper;
	}

	public int create(NotificationRecordData data) {
		return mapper.create(data);
	}

	public int update(NotificationRecordData data, NotificationRecordQueryOptions options) {
		return mapper.update(data, options);
	}

	public int delete(NotificationRecordQueryOptions options) {
		return mapper.delete(options);
	}

	public List<NotificationRecordData> query(NotificationRecordQueryOptions options) {
		return mapper.query(options);
	}
}
