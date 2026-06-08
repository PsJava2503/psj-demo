package com.psj.commerce.security.internal;

import java.time.Instant;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

@Activate(group = CommonConstants.PROVIDER)
public class DubboVerifyProviderFilter implements Filter {

	private static final String DEFAULT_SECRET = "replace-with-shared-secret";

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String callerService = invocation.getAttachment(InternalAuthConstants.ATT_SERVICE);
		String timestamp = invocation.getAttachment(InternalAuthConstants.ATT_TIMESTAMP);
		String nonce = invocation.getAttachment(InternalAuthConstants.ATT_NONCE);
		String signature = invocation.getAttachment(InternalAuthConstants.ATT_SIGNATURE);

		if (isBlank(callerService) || isBlank(timestamp) || isBlank(nonce) || isBlank(signature)) {
			throw new RpcException("missing internal signature attachments");
		}
		if (isTimestampExpired(timestamp)) {
			throw new RpcException("internal signature expired");
		}

		String canonical = DubboSigner.canonical(
				callerService,
				invoker.getInterface().getName(),
				invocation.getMethodName(),
				timestamp,
				nonce
		);
		String expected = DubboSigner.signBase64(resolveSecret(), canonical);
		if (!DubboSigner.safeEquals(expected, signature)) {
			throw new RpcException("invalid internal signature");
		}

		return invoker.invoke(invocation);
	}

	private boolean isTimestampExpired(String timestamp) {
		long ts = Long.parseLong(timestamp);
		long now = Instant.now().toEpochMilli();
		return Math.abs(now - ts) > InternalAuthConstants.DEFAULT_ALLOWED_SKEW_MS;
	}

	private String resolveSecret() {
		String secretFromProperty = System.getProperty("internal.auth.secret");
		if (secretFromProperty != null && !secretFromProperty.isBlank()) {
			return secretFromProperty;
		}
		String secretFromEnv = System.getenv("INTERNAL_AUTH_SECRET");
		if (secretFromEnv != null && !secretFromEnv.isBlank()) {
			return secretFromEnv;
		}
		return DEFAULT_SECRET;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

}
