package com.commerce.agent.infrastructure.vector;

import com.commerce.agent.config.MilvusProperties;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.CollectionSchemaParam;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.index.CreateIndexParam;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "agent.rag.use-milvus", havingValue = "true")
public class MilvusConfig {

	private MilvusServiceClient client;

	@Bean
	public MilvusServiceClient milvusServiceClient(MilvusProperties properties) {
		ConnectParam.Builder builder = ConnectParam.newBuilder()
				.withHost(properties.getHost())
				.withPort(properties.getPort());
		if (StringUtils.hasText(properties.getUsername())) {
			builder.withAuthorization(properties.getUsername(), properties.getPassword());
		}
		client = new MilvusServiceClient(builder.build());
		ensureCollection(client, properties);
		return client;
	}

	@PreDestroy
	public void close() {
		if (client != null) {
			client.close();
		}
	}

	private void ensureCollection(MilvusServiceClient client, MilvusProperties properties) {
		R<Boolean> exists = client.hasCollection(HasCollectionParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.build());
		if (exists.getStatus() != 0) {
			throw new IllegalStateException("Failed to check Milvus collection: " + exists.getMessage());
		}
		if (Boolean.TRUE.equals(exists.getData())) {
			return;
		}
		CollectionSchemaParam schema = CollectionSchemaParam.newBuilder()
				.withEnableDynamicField(false)
				.addFieldType(FieldType.newBuilder()
						.withName("id")
						.withDataType(DataType.VarChar)
						.withMaxLength(256)
						.withPrimaryKey(true)
						.build())
				.addFieldType(FieldType.newBuilder()
						.withName("source")
						.withDataType(DataType.VarChar)
						.withMaxLength(512)
						.build())
				.addFieldType(FieldType.newBuilder()
						.withName("chunkIndex")
						.withDataType(DataType.Int64)
						.build())
				.addFieldType(FieldType.newBuilder()
						.withName("title")
						.withDataType(DataType.VarChar)
						.withMaxLength(512)
						.build())
				.addFieldType(FieldType.newBuilder()
						.withName("content")
						.withDataType(DataType.VarChar)
						.withMaxLength(8192)
						.build())
				.addFieldType(FieldType.newBuilder()
						.withName("vector")
						.withDataType(DataType.FloatVector)
						.withDimension(properties.getDimension())
						.build())
				.build();
		R<RpcStatus> created = client.createCollection(CreateCollectionParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.withDescription("Commerce agent knowledge base")
				.withSchema(schema)
				.withShardsNum(2)
				.build());
		if (created.getStatus() != 0) {
			throw new IllegalStateException("Failed to create Milvus collection: " + created.getMessage());
		}
		R<RpcStatus> indexed = client.createIndex(CreateIndexParam.newBuilder()
				.withCollectionName(properties.getCollection())
				.withFieldName("vector")
				.withIndexType(IndexType.IVF_FLAT)
				.withMetricType(MetricType.L2)
				.withExtraParam("{\"nlist\":128}")
				.withSyncMode(Boolean.FALSE)
				.build());
		if (indexed.getStatus() != 0) {
			throw new IllegalStateException("Failed to create Milvus index: " + indexed.getMessage());
		}
	}
}
