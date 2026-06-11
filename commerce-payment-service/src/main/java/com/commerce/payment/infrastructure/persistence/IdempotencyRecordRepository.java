package com.commerce.payment.infrastructure.persistence;

import com.commerce.payment.domain.model.IdempotencyRecordQueryOptions;
import com.commerce.payment.infrastructure.persistence.mapper.IdempotencyRecordDynamicMapper;
import com.commerce.payment.infrastructure.persistence.model.IdempotencyRecordData;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class IdempotencyRecordRepository {

	private final IdempotencyRecordDynamicMapper idempotencyRecordDynamicMapper;

	public IdempotencyRecordRepository(IdempotencyRecordDynamicMapper idempotencyRecordDynamicMapper) {
		this.idempotencyRecordDynamicMapper = idempotencyRecordDynamicMapper;
	}

	public int create(IdempotencyRecordData data) {
		return idempotencyRecordDynamicMapper.create(data);
	}

	public int update(IdempotencyRecordData data, IdempotencyRecordQueryOptions options) {
		return idempotencyRecordDynamicMapper.update(data, options);
	}

	public int delete(IdempotencyRecordQueryOptions options) {
		return idempotencyRecordDynamicMapper.delete(options);
	}

	public List<IdempotencyRecordData> query(IdempotencyRecordQueryOptions options) {
		return idempotencyRecordDynamicMapper.query(options);
	}
}
